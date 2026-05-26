const API = 'http://localhost:8080';

const display = document.getElementById('display');
const expressaoEl = document.getElementById('expressao');
const erroEl = document.getElementById('erro');
const historicoLista = document.getElementById('historico-lista');

document.querySelectorAll('.btn').forEach(btn => {
    btn.addEventListener('click', () => clicar(btn.textContent.trim()));
});

document.querySelectorAll('.btn1').forEach(btn1 => {
    btn1.addEventListener('click', () => clicar(btn1.textContent.trim()));
});

function clicar(valor) {
    limparErro();
    switch (valor) {
        case '=':   calcular();           break;
        case 'C':   limparTela();         break;
        case 'HC':  limparHistorico();    break;
        case '√':   raizQuadrada();       break;
        case 'sin': trigonometria('sin'); break;
        case 'cos': trigonometria('cos'); break;
        case 'tan': trigonometria('tan'); break;
        default:
            if (display.textContent === '0') display.textContent = valor;
            else display.textContent += valor;
    }
}

async function calcular() {
    const expressao = display.textContent;
    if (!expressao || expressao === '0') {
        mostrarErro('Digite uma operação!');
        return;
    }
    try {
        const response = await fetch(`${API}/calcular`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ expressao: expressao })
        });
        const data = await response.json();
        expressaoEl.textContent = expressao + ' =';
        display.textContent = data.resultado;
        carregarHistorico();
    } catch (e) {
        mostrarErro('Erro ao conectar com o servidor Java!');
    }
}

async function raizQuadrada() {
    const valor = display.textContent;
    const expressao = '√' + valor;
    try {
        const response = await fetch(`${API}/calcular`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ expressao: expressao })
        });
        const data = await response.json();
        expressaoEl.textContent = expressao + ' =';
        display.textContent = data.resultado;
        carregarHistorico();
    } catch (e) {
        mostrarErro('Erro ao conectar com o servidor Java!');
    }
}

async function trigonometria(fn) {
    const valor = display.textContent;
    const expressao = fn + '(' + valor + ')';
    try {
        const response = await fetch(`${API}/calcular`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ expressao: expressao })
        });
        const data = await response.json();
        expressaoEl.textContent = expressao + ' =';
        display.textContent = data.resultado;
        carregarHistorico();
    } catch (e) {
        mostrarErro('Erro ao conectar com o servidor Java!');
    }
}

function limparTela() {
    display.textContent = '0';
    expressaoEl.textContent = '';
}

function limparHistorico() {
    const overlay = document.getElementById('modal-overlay');
    overlay.style.display = 'flex';

    document.getElementById('modal-confirmar').onclick = async () => {
        overlay.style.display = 'none';
        try {
            await fetch(`${API}/historico`, { method: 'DELETE' });
            carregarHistorico();
        } catch (e) {
            mostrarErro('Erro ao conectar com o servidor Java!');
        }
    };

    document.getElementById('modal-cancelar').onclick = () => {
        overlay.style.display = 'none';
    };
}

async function carregarHistorico() {
    try {
        const response = await fetch(`${API}/historico`);
        const lista = await response.json();
        historicoLista.innerHTML = '';
        [...lista].reverse().forEach(item => {
            const div = document.createElement('div');
            div.className = 'hist-item';
            div.innerHTML = `<span>${item.expressao}</span>${item.resultado}`;
            historicoLista.appendChild(div);
        });
    } catch (e) {
        console.log('Histórico indisponível');
    }
}

function mostrarErro(msg) {
    erroEl.textContent = msg;
    setTimeout(() => erroEl.textContent = '', 2500);
}

function limparErro() {
    erroEl.textContent = '';
}

carregarHistorico();