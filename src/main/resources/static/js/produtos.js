const API_URL = "http://localhost:8080/produtos";

async function carregarProdutos() {
  const res = await fetch(API_URL);
  const produtos = await res.json();
  const tbody = document.querySelector("tbody");
  tbody.innerHTML = "";
  produtos.forEach(p => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${p.id}</td>
      <td>${p.nome}</td>
      <td>R$ ${p.preco.toFixed(2)}</td>
      <td><button onclick="deletar(${p.id})">Excluir</button></td>
    `;
    tbody.appendChild(tr);
  });
}

async function adicionarProduto() {
  const nome = document.getElementById("nome").value;
  const preco = document.getElementById("preco").value;
  if (!nome || !preco) return alert("Preencha os campos!");

  await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ nome, preco })
  });

  document.getElementById("nome").value = "";
  document.getElementById("preco").value = "";
  carregarProdutos();
}

async function deletar(id) {
  await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  carregarProdutos();
}

document.getElementById("adicionar").addEventListener("click", adicionarProduto);

carregarProdutos();
