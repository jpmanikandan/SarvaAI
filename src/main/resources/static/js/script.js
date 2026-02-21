const chatHistory = document.getElementById('chatHistory');
const userInput = document.getElementById('userInput');
const sendBtn = document.getElementById('sendBtn');
const recordBtn = document.getElementById('recordBtn');
const voiceStatus = document.getElementById('voiceStatus');
const outputLangSelect = document.getElementById('outputLanguage');
const sidebar = document.getElementById('sidebar');
const sidebarToggle = document.getElementById('sidebarToggle');
const newChatBtn = document.getElementById('newChatBtn');
const chatList = document.getElementById('chatList');

let currentSessionId = localStorage.getItem('lastSessionId') || Date.now().toString();
let mediaRecorder;
let audioChunks = [];

const sidebarBackdrop = document.getElementById('sidebarBackdrop');
const closeSidebarBtn = document.getElementById('closeSidebarBtn');

// --- Sidebar & History Logic ---

function initSidebar() {
    // Initial state setup
    if (window.innerWidth <= 992) {
        sidebar.classList.add('collapsed');
        sidebarBackdrop?.classList.remove('show');
    } else {
        sidebar.classList.remove('collapsed');
    }

    // Toggle button in header
    sidebarToggle?.addEventListener('click', (e) => {
        e.stopPropagation();
        toggleSidebar();
    });

    // Close button inside sidebar (mobile only)
    closeSidebarBtn?.addEventListener('click', () => {
        toggleSidebar();
    });

    // Backdrop click to close
    sidebarBackdrop?.addEventListener('click', () => {
        if (!sidebar.classList.contains('collapsed')) {
            toggleSidebar();
        }
    });

    newChatBtn?.addEventListener('click', () => {
        startNewChat();
    });

    loadChatHistory();
}

function toggleSidebar() {
    sidebar.classList.toggle('collapsed');
    if (window.innerWidth <= 992) {
        sidebarBackdrop?.classList.toggle('show');
    }
}

function startNewChat() {
    currentSessionId = Date.now().toString();
    localStorage.setItem('lastSessionId', currentSessionId);
    chatHistory.innerHTML = `
        <div class="welcome-msg text-center my-auto">
            <i class="fas fa-robot fa-3x text-info mb-3"></i>
            <h2>Welcome to Sarva AI</h2>
            <p class="text-muted">I am your Universal Expert. Ask me anything about Law, Health, Finance, or learn English with Miss Nova.</p>
            <div class="d-flex justify-content-center gap-2 mt-4 flex-wrap">
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Can you help me with legal advice?">#Lawbotix</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="How is the stock market performing?">#ArthAI</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Teach me something new in Physics.">#MissNova</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="How does IoT automation work?">#IoT</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Tips for a healthy lifestyle?">#Health</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Find me the best price for a laptop.">#Commerce</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Help me debug this code.">#Dev</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Looking for a life partner.">#Matrimony</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="How do I do the mountain pose?">#Yoga</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Give me a chest workout plan.">#Gym</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Suggest a high protein diet.">#Diet</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Java interview questions on collections.">#Java</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Explain Thales Theorem.">#SSLC</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Explain integration by parts.">#12thStd</span>
                <span class="badge rounded-pill bg-outline-info text-info border border-info p-2 agent-badge" role="button" data-query="Analyze this Java exception: NullPointerException at line 45.">#LogAnalyzer</span>
            </div>
        </div>
    `;
    reattachBadgeListeners();
    loadChatHistory();
    userInput.focus();
}

async function saveMessageToHistory(sender, text, expert = null) {
    // Create session if it doesn't exist
    if (!currentSessionId) {
        currentSessionId = Date.now().toString();
    }

    try {
        // Check if session exists, create if not
        const sessionCheck = await fetch(`/api/history/sessions/${currentSessionId}`);
        if (!sessionCheck.ok) {
            // Create new session
            let cleanTitle = text.replace(/^\[AGENT:.*?\]\s*/, '');
            const title = cleanTitle.substring(0, 30) + (cleanTitle.length > 30 ? '...' : '');

            await fetch('/api/history/sessions', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ title })
            });
            currentSessionId = (await fetch('/api/history/sessions').then(r => r.json()))[0]?.id || currentSessionId;
        }

        // Save message
        await fetch(`/api/history/sessions/${currentSessionId}/messages`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ sender, content: text, expertName: expert })
        });

        renderChatList();
    } catch (error) {
        console.error('Error saving message:', error);
    }
}

async function renderChatList() {
    if (!chatList) return;
    chatList.innerHTML = '<div class="text-center p-4 text-muted"><small>Loading...</small></div>';

    try {
        const response = await fetch('/api/history/sessions');
        const sessions = await response.json();

        chatList.innerHTML = '';

        if (sessions.length === 0) {
            chatList.innerHTML = '<div class="text-center p-4 text-muted"><small>No history yet</small></div>';
            return;
        }

        sessions.forEach(session => {
            const item = document.createElement('div');
            const isActive = session.id === currentSessionId;
            item.className = `chat-history-item ${isActive ? 'active' : ''}`;

            // Use fallbacks for title and date to avoid 'undefined'
            const displayTitle = session.title || "New Chat";
            const displayDate = new Date(session.updatedAt).toLocaleString();

            item.innerHTML = `
            <div class="chat-title">${displayTitle}</div>
            <div class="chat-date">${displayDate}</div>
            <span class="delete-chat" title="Delete Chat">
                <i class="fas fa-trash-alt"></i>
            </span>
        `;

            item.addEventListener('click', (e) => {
                if (e.target.closest('.delete-chat')) {
                    e.stopPropagation();
                    deleteChat(session.id);
                } else {
                    loadChat(session.id);
                }
            });

            chatList.appendChild(item);
        });
    } catch (error) {
        console.error('Error loading chat list:', error);
        chatList.innerHTML = '<div class="text-center p-4 text-danger"><small>Error loading history</small></div>';
    }
}

function loadChatHistory() {
    renderChatList();
    const history = JSON.parse(localStorage.getItem('sarva_chat_history') || '{}');
    if (history[currentSessionId]) {
        loadChat(currentSessionId);
    }
}

async function loadChat(sessionId) {
    try {
        const response = await fetch(`/api/history/sessions/${sessionId}/messages`);
        if (!response.ok) {
            console.error('Session not found:', sessionId);
            startNewChat();
            return;
        }

        const messages = await response.json();
        currentSessionId = sessionId;
        chatHistory.innerHTML = '';

        messages.forEach(msg => {
            appendMessage(msg.sender, msg.content, msg.expertName, false);
        });

        // Final scroll adjust
        chatHistory.scrollTop = 0;
        renderChatList();

        // Auto-close sidebar on mobile
        if (window.innerWidth <= 992 && !sidebar.classList.contains('collapsed')) {
            toggleSidebar();
        }
    } catch (error) {
        console.error('Error loading chat:', error);
        startNewChat();
    }
}

async function deleteChat(sessionId) {
    if (!confirm('Are you sure you want to delete this chat?')) return;

    try {
        await fetch(`/api/history/sessions/${sessionId}`, {
            method: 'DELETE'
        });

        if (currentSessionId === sessionId) {
            startNewChat();
        } else {
            renderChatList();
        }
    } catch (error) {
        console.error('Error deleting chat:', error);
    }
}

// --- Badge Listener Logic ---
function reattachBadgeListeners() {
    document.querySelectorAll('.agent-badge').forEach(badge => {
        badge.addEventListener('click', () => {
            const query = badge.getAttribute('data-query');
            if (query) {
                userInput.value = query;
                userInput.focus();
            }
        });
    });
}

// --- Core Functions ---

function appendMessage(sender, text, expert = null, save = true) {
    const wrapper = document.createElement('div');
    wrapper.className = 'd-flex flex-column w-100';

    const msgDiv = document.createElement('div');
    msgDiv.className = `message ${sender === 'user' ? 'user-message' : 'ai-message shadow-sm'}`;

    if (expert) {
        const tag = document.createElement('span');
        tag.className = 'expert-tag';
        tag.innerText = expert;
        msgDiv.appendChild(tag);
    }

    const content = document.createElement('div');
    content.innerHTML = typeof marked !== 'undefined' ? marked.parse(text) : text;
    msgDiv.appendChild(content);

    if (typeof hljs !== 'undefined') {
        content.querySelectorAll('pre code').forEach((block) => {
            hljs.highlightElement(block);
            addCopyButton(block);
        });
    }

    wrapper.appendChild(msgDiv);
    chatHistory.insertBefore(wrapper, chatHistory.firstChild);

    // Hide welcome message
    const welcome = document.querySelector('.welcome-msg');
    if (welcome) welcome.style.display = 'none';

    if (save) {
        saveMessageToHistory(sender, text, expert);
    }
}

async function sendMessage(text) {
    if (!text.trim()) return;

    if (!currentSessionId) currentSessionId = Date.now().toString();

    appendMessage('user', text);

    userInput.value = '';
    showTypingIndicator();

    try {
        const response = await fetch('/api/chat/stream', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                query: text,
                outputLanguage: outputLangSelect.value
            })
        });

        const reader = response.body.getReader();
        const decoder = new TextDecoder();
        let aiMessageDiv = null;
        let currentText = "";
        let expertName = "AI";
        let isFirstChunk = true;

        while (true) {
            const { done, value } = await reader.read();
            if (done) break;

            const chunk = decoder.decode(value, { stream: true });
            const lines = chunk.split('\n');

            for (const line of lines) {
                if (line.startsWith('data:')) {
                    let data = line.substring(5);
                    if (data.length === 0) {
                        currentText += "\n";
                    } else {
                        currentText += data;
                    }
                }
            }

            if (isFirstChunk) {
                const match = currentText.match(/^\[AGENT:(.*?)\]\s*/);
                if (match) {
                    expertName = match[1];
                    currentText = currentText.replace(match[0], "");
                    isFirstChunk = false;
                } else if (currentText.length > 50) {
                    isFirstChunk = false;
                }
            }

            if (!aiMessageDiv) {
                removeTypingIndicator();
                appendMessage('ai', currentText, expertName + " Agent Response", false); // Don't save yet
                const messages = document.querySelectorAll('.ai-message');
                aiMessageDiv = messages[0].querySelector('div');
            } else {
                const messages = document.querySelectorAll('.ai-message');
                const contentDiv = messages[0].querySelectorAll('div')[0];

                if (contentDiv) {
                    contentDiv.innerHTML = marked.parse(currentText);
                    contentDiv.querySelectorAll('pre code').forEach((block) => {
                        hljs.highlightElement(block);
                        addCopyButton(block);
                    });
                }

                const expertTag = messages[0].querySelector('.expert-tag');
                if (expertTag && expertTag.innerText !== expertName + " Agent Response") {
                    expertTag.innerText = expertName + " Agent Response";
                }
            }
        }

        // Final save after stream is complete
        saveMessageToHistory('ai', currentText, expertName + " Agent Response");

    } catch (error) {
        removeTypingIndicator();
        console.error('Error:', error);
        const errorText = "Sorry, I encountered an error connecting to the Sarva Core.";
        appendMessage('ai', errorText);
    }
}

// --- Helper Functions ---

function showTypingIndicator() {
    const wrapper = document.createElement('div');
    wrapper.className = 'd-flex flex-column w-100 typing-indicator-wrapper';
    wrapper.innerHTML = `
            <div class="typing-indicator">
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
                <div class="typing-dot"></div>
            </div>
        `;
    chatHistory.insertBefore(wrapper, chatHistory.firstChild);
}

function removeTypingIndicator() {
    const indicator = document.querySelector('.typing-indicator-wrapper');
    if (indicator) indicator.remove();
}

function addCopyButton(codeBlock) {
    const pre = codeBlock.parentNode;
    if (pre.parentNode.classList.contains('code-wrapper')) return;

    const wrapper = document.createElement('div');
    wrapper.className = 'code-wrapper';
    pre.parentNode.insertBefore(wrapper, pre);
    wrapper.appendChild(pre);

    const btn = document.createElement('button');
    btn.className = 'copy-btn';
    btn.innerHTML = '<i class="fas fa-copy"></i> Copy';

    btn.addEventListener('click', () => {
        navigator.clipboard.writeText(codeBlock.innerText).then(() => {
            btn.innerHTML = '<i class="fas fa-check"></i> Copied!';
            btn.classList.add('copied');
            setTimeout(() => {
                btn.innerHTML = '<i class="fas fa-copy"></i> Copy';
                btn.classList.remove('copied');
            }, 2000);
        });
    });

    wrapper.appendChild(btn);
}

// --- Voice Implementation ---

async function startRecording() {
    try {
        const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
        mediaRecorder = new MediaRecorder(stream);
        audioChunks = [];

        mediaRecorder.ondataavailable = (event) => {
            audioChunks.push(event.data);
        };

        mediaRecorder.onstop = async () => {
            const audioBlob = new Blob(audioChunks, { type: 'audio/wav' });
            await sendVoiceChat(audioBlob);
        };

        mediaRecorder.start();
        recordBtn.classList.add('recording');
        voiceStatus.classList.remove('d-none');
    } catch (err) {
        alert('Could not access microphone: ' + err);
    }
}

function stopRecording() {
    mediaRecorder.stop();
    recordBtn.classList.remove('recording');
    voiceStatus.classList.add('d-none');
}

async function sendVoiceChat(blob) {
    const voicePlaceholder = "🎙️ Spoken Message...";
    appendMessage('user', voicePlaceholder);

    const formData = new FormData();
    formData.append('file', blob, 'recording.wav');
    formData.append('outputLanguage', outputLangSelect.value);

    try {
        const response = await fetch('/api/voice/chat', {
            method: 'POST',
            body: formData
        });

        if (response.ok) {
            const audioBlob = await response.blob();
            const audioUrl = URL.createObjectURL(audioBlob);
            const audio = new Audio(audioUrl);
            audio.play();

            const aiText = "I've responded via voice. Listen closely!";
            appendMessage('ai', aiText, "Miss Nova");
        }
    } catch (error) {
        console.error('Voice Error:', error);
        appendMessage('ai', "Error processing voice command.");
    }
}

// --- Event Listeners ---

sendBtn.addEventListener('click', () => sendMessage(userInput.value));

userInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') sendMessage(userInput.value);
});

recordBtn.addEventListener('click', () => {
    if (mediaRecorder && mediaRecorder.state === 'recording') {
        stopRecording();
    } else {
        startRecording();
    }
});

// Initialize Everything
document.addEventListener('DOMContentLoaded', () => {
    reattachBadgeListeners();
    initSidebar();
});

