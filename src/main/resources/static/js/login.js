document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value.trim();
            const senha = document.getElementById('senha').value.trim();

            if (!email || !senha) {
                alert('Preencha todos os campos.');
                return;
            }

            // Detecta tipo de usuário pelo localStorage ou adiciona um seletor se necessário
            let tipo = localStorage.getItem('loginTipo') || 'usuario';
            // Se houver um campo de seleção de tipo, use ele
            const tipoRadio = document.querySelector('input[name="tipo"]:checked');
            if (tipoRadio) tipo = tipoRadio.value;

            try {
                const response = await fetch('http://localhost:8080/api/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({
                        email: email,
                        senha: senha,
                        tipo: tipo
                    })
                });

                const data = await response.json();

                if (response.ok && data.sucesso) {
                    // Salva dados do usuário logado
                    localStorage.setItem('userId', data.id);
                    localStorage.setItem('userName', data.nome);
                    localStorage.setItem('userEmail', data.email);
                    localStorage.setItem('userRole', data.tipo);

                    alert(`Bem-vindo(a), ${data.nome}!`);
                    window.location.href = '../html/mapa.html';
                } else {
                    alert(data.mensagem || 'Email ou senha incorretos.');
                }
            } catch (error) {
                alert('Erro ao conectar com o servidor.');
                console.error(error);
            }
        });
    }
});