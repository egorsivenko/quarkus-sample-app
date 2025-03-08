document.addEventListener('DOMContentLoaded', function () {
    const scopeSearch = document.createElement('input')
    scopeSearch.type = 'text';
    scopeSearch.id = 'scopeSearch';
    scopeSearch.ariaLabel = 'scopeSearch';
    scopeSearch.placeholder = 'Search scopes...';

    const scopesLabel = document.querySelector('label[for=scopes]');
    scopesLabel.insertAdjacentElement("afterend", scopeSearch);

    const scopesSelect = document.getElementById('scopes');
    const options = Array.from(scopesSelect.options);

    scopeSearch.addEventListener('input', function () {
        const query = this.value.toLowerCase();

        options.forEach(option => {
            const text = option.text.toLowerCase();
            if (text.includes(query)) {
                option.style.display = '';
            } else {
                option.style.display = 'none';
            }
        });
    });
});