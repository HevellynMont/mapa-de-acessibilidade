const API_BASE_URL = "http://localhost:8080";

const ENUM_TAGS = {
    RAMPA: { label: "Rampa de Acesso", negativa: false },
    ELEVADOR: { label: "Elevador", negativa: false },
    BANHEIRO: { label: "Banheiro Adaptado", negativa: false },
    PISO_TATIL: { label: "Piso Tátil", negativa: false },
    LIBRAS: { label: "Intérprete de Libras", negativa: false },
    ESTACIONAMENTO: { label: "Estacionamento", negativa: false },
    ENTRADA_LARGA: { label: "Entrada Larga", negativa: false },
    BRAILLE: { label: "Sinalização em Braille", negativa: false },
    INADEQUADO: { label: "Local Inadequado / Não Acessível", negativa: true }
};

async function fetchAPI(endpoint, options = {}) {
    const defaultHeaders = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    const config = {
        ...options,
        headers: { ...defaultHeaders, ...options.headers }
    };

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, config);
        
        if (response.status === 204) return null;

        if (!response.ok) {
            const data = await response.json().catch(() => ({}));
            throw new Error(data.message || `Erro ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error(error);
        throw error;
    }
}