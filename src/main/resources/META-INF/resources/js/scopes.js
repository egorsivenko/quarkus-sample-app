document.addEventListener('DOMContentLoaded', function () {
    const scopeSearch = document.getElementById('scopeSearch');
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