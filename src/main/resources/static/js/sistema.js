document.addEventListener("DOMContentLoaded", function () {
configurarBuscaCep();
configurarMascaraTelefone();
configurarMascaraCelular();
configurarMascaraCpf();
configurarMascaraCep();
configurarMascaraValor();
configurarAutoExpandTextareas();
atualizarUfCliente();
});

function alternarSubmenu(id) {
const submenu = document.getElementById(id);
if (!submenu) return;

submenu.classList.toggle("aberto");
}

function alternarDetalhes(id) {
const detalhesSelecionado = document.getElementById(id);
if (!detalhesSelecionado) return;

document.querySelectorAll(".cliente-detalhes").forEach(function (detalhe) {
    if (detalhe.id !== id) {
        detalhe.classList.remove("aberto");
    }
});

detalhesSelecionado.classList.toggle("aberto");
}

function toggleSenha() {
const campoSenha = document.getElementById("senhaHash");
const iconeSenha = document.getElementById("iconeSenha");

if (!campoSenha || !iconeSenha) return;

if (campoSenha.type === "password") {
    campoSenha.type = "text";
    iconeSenha.className = "bi bi-eye-slash";
} else {
    campoSenha.type = "password";
    iconeSenha.className = "bi bi-eye";
}
}

function configurarMascaraTelefone() {
const camposTelefone = document.querySelectorAll("#telefone, input[name='telefone']");

camposTelefone.forEach((campo) => {
    campo.addEventListener("input", function () {
        campo.value = aplicarMascaraTelefone(campo.value);
    });

    campo.addEventListener("blur", function () {
        campo.value = aplicarMascaraTelefone(campo.value);
    });
});
}

function configurarMascaraCelular() {
const camposCelular = document.querySelectorAll("#celular, input[name='celular']");
camposCelular.forEach((campo) => {
campo.addEventListener("input", function () {
campo.value = aplicarMascaraTelefone(campo.value);
});
});
}

function aplicarMascaraTelefone(valor) {
let numeros = valor.replace(/\D/g, "");

if (numeros.length > 11) {
    numeros = numeros.substring(0, 11);
}

if (numeros.length <= 2) {
    return numeros.length ? `(${numeros}` : "";
}

if (numeros.length <= 6) {
    return `(${numeros.substring(0, 2)}) ${numeros.substring(2)}`;
}

if (numeros.length <= 10) {
    return `(${numeros.substring(0, 2)}) ${numeros.substring(2, 6)}-${numeros.substring(6)}`;
}

return `(${numeros.substring(0, 2)}) ${numeros.substring(2, 7)}-${numeros.substring(7)}`;
}

function configurarMascaraCpf() {
const camposCpf = document.querySelectorAll("#cpf, input[name='cpf']");

camposCpf.forEach((campo) => {
    campo.addEventListener("input", function () {
        campo.value = aplicarMascaraCpf(campo.value);
    });

    campo.addEventListener("blur", function () {
        campo.value = aplicarMascaraCpf(campo.value);
    });
});
}

function aplicarMascaraCpf(valor) {
let numeros = valor.replace(/\D/g, "");

if (numeros.length > 11) {
    numeros = numeros.substring(0, 11);
}

if (numeros.length <= 3) {
    return numeros;
}

if (numeros.length <= 6) {
    return `${numeros.substring(0, 3)}.${numeros.substring(3)}`;
}

if (numeros.length <= 9) {
    return `${numeros.substring(0, 3)}.${numeros.substring(3, 6)}.${numeros.substring(6)}`;
}

return `${numeros.substring(0, 3)}.${numeros.substring(3, 6)}.${numeros.substring(6, 9)}-${numeros.substring(9)}`;
}

function configurarMascaraCep() {
const camposCep = document.querySelectorAll("#cep, input[name='cep']");

camposCep.forEach((campo) => {
    campo.addEventListener("input", function () {
        campo.value = aplicarMascaraCep(campo.value);
    });

    campo.addEventListener("blur", function () {
        campo.value = aplicarMascaraCep(campo.value);
    });
});
}

function aplicarMascaraCep(valor) {
let numeros = valor.replace(/\D/g, "");

if (numeros.length > 8) {
    numeros = numeros.substring(0, 8);
}

if (numeros.length <= 5) {
    return numeros;
}

return `${numeros.substring(0, 5)}-${numeros.substring(5)}`;
}

function configurarMascaraValor() {
const camposValor = document.querySelectorAll("#valor, input[name='valor']");

camposValor.forEach((campo) => {
    campo.addEventListener("input", function () {
        campo.value = aplicarMascaraValor(campo.value);
    });

    campo.addEventListener("blur", function () {
        campo.value = aplicarMascaraValor(campo.value);
    });
});
}

function aplicarMascaraValor(valor) {
let numeros = valor.replace(/\D/g, "");

if (!numeros) {
    return "";
}

while (numeros.length < 3) {
    numeros = "0" + numeros;
}

const parteInteira = numeros.slice(0, -2).replace(/^0+(?=\d)/, "");
const parteDecimal = numeros.slice(-2);

return `${parteInteira || "0"},${parteDecimal}`;
}

function configurarBuscaCep() {
const campoCep = document.getElementById("cep");
if (!campoCep) return;

campoCep.addEventListener("blur", async function () {
    const cepLimpo = campoCep.value.replace(/\D/g, "");

    if (cepLimpo.length !== 8) {
        return;
    }

    try {
        const response = await fetch(`https://viacep.com.br/ws/${cepLimpo}/json/`);
        const dados = await response.json();

        if (dados.erro) {
            alert("CEP não encontrado.");
            return;
        }

        preencherCamposEndereco(dados);
        selecionarCidadeNoCombo(dados.localidade, dados.uf);

    } catch (error) {
        console.error("Erro ao buscar CEP:", error);
        alert("Não foi possível localizar os dados pelo CEP.");
    }
});
}

function preencherCamposEndereco(dados) {
const campoEndereco = document.getElementById("endereco");
const campoBairro = document.getElementById("bairro");

if (campoEndereco) {
    campoEndereco.value = dados.logradouro || "";
}

if (campoBairro) {
    campoBairro.value = dados.bairro || "";
}
}

function selecionarCidadeNoCombo(localidade, uf) {
const selectCidade = document.getElementById("cidade");
if (!selectCidade) return;

const localidadeNormalizada = normalizarTexto(localidade);
const ufNormalizada = normalizarTexto(uf);

let encontrou = false;

for (let i = 0; i < selectCidade.options.length; i++) {
    const option = selectCidade.options[i];
    const textoOption = normalizarTexto(option.text);

    const bateCidade =
        textoOption === `${localidadeNormalizada} - ${ufNormalizada}` ||
        textoOption === localidadeNormalizada ||
        textoOption.includes(`${localidadeNormalizada} - ${ufNormalizada}`) ||
        textoOption.includes(localidadeNormalizada);

    if (bateCidade) {
        selectCidade.selectedIndex = i;
        encontrou = true;
        break;
    }
}

if (!encontrou) {
    console.warn(`Cidade não encontrada no select: ${localidade}/${uf}`);
    alert(`CEP localizado, mas a cidade ${localidade}/${uf} não foi encontrada na lista. Selecione manualmente.`);
}
}

function normalizarTexto(texto) {
if (!texto) return "";

return texto
    .toString()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .trim()
    .toUpperCase();
}

function atualizarUfCliente() {
const selectCidade = document.getElementById("cidade");
const campoUf = document.getElementById("ufCliente");
if (!selectCidade || !campoUf) return;
const option = selectCidade.options[selectCidade.selectedIndex];
campoUf.value = option ? (option.getAttribute("data-uf") || "") : "";
}

function configurarAutoExpandTextareas() {
const textareas = document.querySelectorAll(".auto-expand");

textareas.forEach((textarea) => {
    ajustarAlturaTextarea(textarea);

    textarea.addEventListener("input", function () {
        ajustarAlturaTextarea(textarea);
    });
});
}

function ajustarAlturaTextarea(textarea) {
textarea.style.height = "auto";
textarea.style.height = textarea.scrollHeight + "px";
}

function selecionarCliente(idCliente, nomeCliente) {
const campoId = document.getElementById("idCliente");
const campoNome = document.getElementById("nomeClienteSelecionado");

if (campoId) {
    campoId.value = idCliente;
}

if (campoNome) {
    campoNome.value = nomeCliente;
    campoNome.classList.add("cliente-selecionado");
}

fecharModalClientes();
}

function abrirModalClientes() {
const modal = document.getElementById("modalClientes");

if (modal) {
    modal.classList.add("aberto");
}
}

function fecharModalClientes() {
const modal = document.getElementById("modalClientes");

if (modal) {
    modal.classList.remove("aberto");
}
}

function filtrarClientesModal() {
const inputFiltro = document.getElementById("filtroClienteModal");
const linhas = document.querySelectorAll("#tabelaClientesModal tbody tr");

if (!inputFiltro || !linhas.length) return;

const filtro = inputFiltro.value.trim().toUpperCase();

linhas.forEach((linha) => {
    const texto = (linha.getAttribute("data-busca") || "").toUpperCase();
    linha.style.display = texto.includes(filtro) ? "" : "none";
});
}