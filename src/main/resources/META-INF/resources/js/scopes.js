document.addEventListener("DOMContentLoaded", () => {
    const scopeInput = document.getElementById("scopeInput");
    const addScopeButton = document.getElementById("addScopeButton");
    const scopeContainer = document.getElementById("scopeContainer");
    const scopesInput = document.getElementById("scopesInput");
    const scopes = new Set();

    scopeInput.addEventListener("input", () => {
        addScopeButton.disabled = scopeInput.value.trim() === "";
    });

    addScopeButton.addEventListener("click", () => {
        const scopeName = scopeInput.value.toLowerCase().replaceAll(",", "").trim();
        if (scopeName !== "" && !scopes.has(scopeName)) {
            scopes.add(scopeName);
            addScopeTile(scopeName);
            updateScopesInput();
            scopeInput.removeAttribute("aria-invalid");
        } else {
            scopeInput.setAttribute("aria-invalid", "true");
        }
        scopeInput.value = "";
        addScopeButton.disabled = true;
    });

    function updateScopesInput() {
        scopesInput.value = Array.from(scopes).join(",");
    }

    function addScopeTile(scopeName) {
        const tile = document.createElement("span");
        tile.className = "scope-tile";
        tile.textContent = scopeName;

        const removeButton = document.createElement("button");
        removeButton.textContent = "x";
        removeButton.className = "remove-scope";
        removeButton.addEventListener("click", () => {
            scopes.delete(scopeName);
            tile.remove();
            updateScopesInput();
        });

        tile.appendChild(removeButton);
        scopeContainer.appendChild(tile);
    }
});