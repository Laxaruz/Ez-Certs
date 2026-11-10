const toggle = document.getElementById('toggle-password');
const password = document.getElementById('password');
if (toggle && password) {
  toggle.addEventListener('click', () => {
    const showing = password.type === 'password';
    password.type = showing ? 'text' : 'password';
    toggle.textContent = showing ? 'Ocultar' : 'Mostrar';
    toggle.setAttribute('aria-label', showing ? 'Ocultar contraseña' : 'Mostrar contraseña');
    toggle.setAttribute('aria-pressed', String(showing));
  });
}

const certificateSearch = document.getElementById('certificate-search');
const certificateType = document.getElementById('certificate-type');
if (certificateSearch && certificateType) {
  const rows = [...document.querySelectorAll('.cert-row')];
  const empty = document.getElementById('certificate-empty-filter');
  const filterRows = () => {
    const query = certificateSearch.value.trim().toLocaleLowerCase('es');
    const type = certificateType.value;
    let visible = 0;
    for (const row of rows) {
      const matches = row.textContent.toLocaleLowerCase('es').includes(query)
        && (!type || row.dataset.type === type);
      row.hidden = !matches;
      if (matches) visible++;
    }
    if (empty) empty.hidden = visible > 0 || rows.length === 0;
  };
  certificateSearch.addEventListener('input', filterRows);
  certificateType.addEventListener('change', filterRows);
}
