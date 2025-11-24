document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value.trim();
            const senha = document.getElementById('senha').value.trim();
            const btn = loginForm.querySelector('button');

            if (!email || !senha) {
                alert('Preencha todos os campos.');
                return;
            }

            realizarLogin(email, senha, btn);
        });
    }

    async function realizarLogin(email, senha, btn) {
        btn.disabled = true;
        btn.textContent = "Entrando...";

        try {
            const usuarios = await fetchAPI('/usuarios');
            let user = usuarios.find(u => u.email === email);
            let role = 'usuario';

            if (!user) {
                const props = await fetchAPI('/proprietarios');
                user = props.find(p => p.email === email);
                role = 'proprietario';
            }

            if (user && (user.senha === senha || !user.senha)) {
                localStorage.setItem('userId', user.id);
                localStorage.setItem('userName', user.nome);
                localStorage.setItem('userEmail', user.email);
                localStorage.setItem('userRole', role);
                
                const avatarUrl = `https://api.dicebear.com/7.x/adventurer/svg?seed=${encodeURIComponent(user.nome)}`;
                localStorage.setItem('userAvatar', avatarUrl);

                window.location.href = '../html/mapa.html';
            } else {
                alert('Email ou senha incorretos.');
            }
        } catch (error) {
            alert("Erro ao conectar com o servidor.");
        } finally {
            btn.disabled = false;
            btn.textContent = "Entrar";
        }
    }
});