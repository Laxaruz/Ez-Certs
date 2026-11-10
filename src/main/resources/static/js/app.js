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
