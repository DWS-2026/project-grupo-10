// Initialize Quill editors
var quillCreateFacility = new Quill('#create-facility-editor', { theme: 'snow' });
var quillCreateClass = new Quill('#create-class-editor', { theme: 'snow' });

// For edit facilities and classes, init when popup opens
document.addEventListener('click', function (e) {
    if (e.target.matches('a[href^="#edit-facility-"]')) {
        const href = e.target.getAttribute('href');
        const id = href.split('-').pop();
        setTimeout(() => {
            if (!document.querySelector('#edit-facility-editor-' + id + ' .ql-editor')) {
                new Quill('#edit-facility-editor-' + id, { theme: 'snow' });
            }
        }, 100);
    }
    if (e.target.matches('a[href^="#edit-class-"]')) {
        const href = e.target.getAttribute('href');
        const id = href.split('-').pop();
        setTimeout(() => {
            if (!document.querySelector('#edit-class-editor-' + id + ' .ql-editor')) {
                new Quill('#edit-class-editor-' + id, { theme: 'snow' });
            }
        }, 100);
    }
});

// Sync content on form submit
document.addEventListener('submit', function (e) {
    if (e.target.matches('#creation-popup form')) {
        document.getElementById('create-facility-desc').value = quillCreateFacility.root.innerHTML;
    }
    if (e.target.matches('#create-class-popup form')) {
        document.getElementById('create-class-desc').value = quillCreateClass.root.innerHTML;
    }
    // For edit forms
    const form = e.target;
    const editorDiv = form.querySelector('.rich-editor');
    if (editorDiv) {
        const id = editorDiv.id;
        const descId = id.replace('editor', 'desc');
        const quill = Quill.find(document.getElementById(id));
        if (quill) {
            document.getElementById(descId).value = quill.root.innerHTML;
        }
    }
});