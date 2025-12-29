(function(){
  // Detect current role (aluno/instrutor) based on body or URL
  function detectRole(){
    var role = (document.body && document.body.dataset && document.body.dataset.role) || '';
    if (role) return role.toLowerCase();
    var path = (window.location && window.location.pathname || '').toLowerCase();
    if (path.indexOf('/instrutor') !== -1) return 'instrutor';
    if (path.indexOf('/aluno') !== -1) return 'aluno';
    return 'default';
  }

  // Create a Bootstrap modal for confirmations, role-scoped
  function ensureModal(role){
    role = role || detectRole();
    var modalId = 'systemConfirmModal';
    var modalClass = 'confirm-modal-default';
    if (role === 'aluno') { modalId = 'systemConfirmModalAluno'; modalClass = 'confirm-modal-aluno'; }
    else if (role === 'instrutor') { modalId = 'systemConfirmModalInstrutor'; modalClass = 'confirm-modal-instrutor'; }

    var modal = document.getElementById(modalId) || document.getElementById('systemConfirmModal');
    if (modal && modal.id === modalId) return modal;
    if (modal && modal.id === 'systemConfirmModal' && role === 'default') return modal;

    // Create new modal element
    modal = document.createElement('div');
    modal.id = modalId;
    modal.className = 'modal fade ' + modalClass;
    modal.tabIndex = -1;
    modal.setAttribute('aria-hidden','true');
    modal.innerHTML = [
      '<div class="modal-dialog modal-dialog-centered">',
      '  <div class="modal-content">',
      '    <div class="modal-header">',
      '      <h5 class="modal-title">Confirmar ação</h5>',
      '      <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>',
      '    </div>',
      '    <div class="modal-body">',
      '      <p class="mb-0" data-role="message"></p>',
      '    </div>',
      '    <div class="modal-footer">',
      '      <button type="button" class="btn btn-secondary" data-action="cancel">Cancelar</button>',
      '      <button type="button" class="btn btn-danger" data-action="confirm">Confirmar</button>',
      '    </div>',
      '  </div>',
      '</div>'
    ].join('');
    document.body.appendChild(modal);
    return modal;
  }

  function showConfirm(message, options){
    options = options || {};
    var confirmText = options.confirmText || 'Confirmar';
    var cancelText = options.cancelText || 'Cancelar';
    var titleText = options.title || 'Confirmar ação';
    var role = (options.role || detectRole());

    var modalEl = ensureModal(role);
    var msgEl = modalEl.querySelector('[data-role="message"]');
    var confirmBtn = modalEl.querySelector('[data-action="confirm"]');
    var cancelBtn = modalEl.querySelector('[data-action="cancel"]');
    var titleEl = modalEl.querySelector('.modal-title');

    msgEl.textContent = message || '';
    titleEl.textContent = titleText;
    confirmBtn.textContent = confirmText;
    cancelBtn.textContent = cancelText;

    return new Promise(function(resolve){
      var bsModal = window.bootstrap && window.bootstrap.Modal ? window.bootstrap.Modal.getOrCreateInstance(modalEl) : null;
      var resolved = false;
      function cleanup(){
        confirmBtn.removeEventListener('click', onConfirm);
        cancelBtn.removeEventListener('click', onCancel);
        modalEl.removeEventListener('hidden.bs.modal', onHidden);
      }
      function onConfirm(){
        resolved = true;
        cleanup();
        if (bsModal) bsModal.hide();
        resolve(true);
      }
      function onCancel(){
        resolved = true;
        cleanup();
        if (bsModal) bsModal.hide();
        resolve(false);
      }
      function onHidden(){
        if (!resolved){ // dismissed by close button or backdrop
          cleanup();
          resolve(false);
        }
      }
      confirmBtn.addEventListener('click', onConfirm);
      cancelBtn.addEventListener('click', onCancel);
      modalEl.addEventListener('hidden.bs.modal', onHidden);
      if (bsModal) {
        bsModal.show();
      } else {
        // Fallback without Bootstrap: simple confirm
        var ok = window.confirm(message || 'Confirmar?');
        cleanup();
        resolve(!!ok);
      }
    });
  }

  function handleConfirmSubmit(evt){
    evt = evt || window.event;
    var form = evt.target || evt.srcElement;
    var message = form.getAttribute('data-confirm') || 'Confirmar ação?';
    if (evt.preventDefault) evt.preventDefault(); else evt.returnValue = false;
    showConfirm(message).then(function(ok){
      if (ok){
        // Avoid re-triggering this handler
        form.removeAttribute('onsubmit');
        // If jQuery present, prefer native submit
        form.submit();
      }
    });
    return false; // always prevent default
  }

  // Expose globals
  window.showConfirm = showConfirm;
  window.handleConfirmSubmit = handleConfirmSubmit;
})();
