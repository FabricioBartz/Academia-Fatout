(function(){
  // Create a Bootstrap modal for confirmations if not present
  function ensureModal(){
    let modal = document.getElementById('systemConfirmModal');
    if (modal) return modal;
    modal = document.createElement('div');
    modal.id = 'systemConfirmModal';
    modal.className = 'modal fade';
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
      '      <p id="systemConfirmMessage" class="mb-0"></p>',
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

    var modalEl = ensureModal();
    var msgEl = modalEl.querySelector('#systemConfirmMessage');
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
