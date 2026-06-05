// ─── Projects ─────────────────────────────────────────────────────────────────
export const projects = [
  {
    id: 'city',
    featured: true,
    title: 'AI Smart City Monitoring',
    subtitle: 'Real-time urban intelligence platform',
    desc: 'A full-stack platform that aggregates real-time traffic sensor data, runs Isolation Forest anomaly detection via FastAPI, and broadcasts WebSocket alerts when risk thresholds are breached.',
    tags: [{ label: 'AI', color: 'purple' }, { label: 'Realtime', color: 'blue' }, { label: 'Full-Stack', color: 'green' }],
    techs: ['FastAPI', 'Spring Boot', 'WebSocket', 'Redis', 'Leaflet', 'Vue 3'],
    previewType: 'city',
    year: '2026',
    status: 'Live',
  },
  {
    id: 'virtudy',
    featured: false,
    title: 'Virtudy',
    subtitle: 'AI avatar study platform',
    desc: 'An interactive study companion powered by an AI avatar that adapts its teaching style to user behaviour and learning pace.',
    tags: [{ label: 'AI', color: 'purple' }, { label: 'EdTech', color: 'blue' }],
    techs: ['Vue 3', 'Spring Boot', 'OpenAI', 'WebRTC'],
    previewType: 'virtudy',
    year: '2025',
    status: 'WIP',
  },
  {
    id: 'mirror',
    featured: false,
    title: 'Mirror',
    subtitle: 'Multiplayer puzzle game',
    desc: 'A browser-based real-time multiplayer game where players solve spatial puzzles using mirrored physics mechanics.',
    tags: [{ label: 'Game', color: 'orange' }, { label: 'Multiplayer', color: 'pink' }],
    techs: ['Unity', 'WebGL', 'Photon', 'C#'],
    previewType: 'mirror',
    year: '2025',
    status: 'Live',
  },
  {
    id: 'fashion',
    featured: false,
    title: 'AI Fashion Assistant',
    subtitle: 'Style intelligence engine',
    desc: 'Multimodal AI that analyses wardrobe photos, detects colour palettes and body type, then generates personalised outfit recommendations.',
    tags: [{ label: 'AI', color: 'purple' }, { label: 'Vision', color: 'blue' }],
    techs: ['Python', 'TensorFlow', 'FastAPI', 'React'],
    previewType: 'fashion',
    year: '2025',
    status: 'Concept',
  },
]

// ─── Music ────────────────────────────────────────────────────────────────────
export const tracks = [
  {
    id: 1, title: 'Neon Circuit', album: 'Signal Paths', genre: 'Electronic', bpm: 128,
    duration: '3:42', released: '2025-11', emoji: '⚡',
    colorA: '#1a1a3e', colorB: '#5b9cf6',
    story: 'Born during a late-night debugging session — the arpeggios mirror the rhythm of a blinking cursor waiting for a compile.',
    lyrics: `Running through circuits of light\nEvery signal finds its way\nIn the noise I hear the night\nFrequencies that never fade\n\nNeon, neon, circuit dreams\nPulse and wire, electric seams`,
  },
  {
    id: 2, title: 'Digital Ghost', album: 'Phantom OS', genre: 'Lo-fi Hip-hop', bpm: 84,
    duration: '2:58', released: '2025-08', emoji: '👻',
    colorA: '#1a1228', colorB: '#a78bfa',
    story: 'An ode to deprecated code — functions that once powered entire apps, now silently drifting in git history.',
    lyrics: `Old functions in the stack\nComments no one reads\nA ghost of old commits\nWhere the legacy bleeds\n\nDigital phantom, dormant code\nStill running on a forgotten node`,
  },
  {
    id: 3, title: 'Phosphene', album: 'Signal Paths', genre: 'Synthwave', bpm: 120,
    duration: '4:10', released: '2025-11', emoji: '🌀',
    colorA: '#1a0a2e', colorB: '#f472b6',
    story: 'Phosphenes are the lights you see when you press your eyes shut. This track is about the things you see only when you disconnect.',
    lyrics: `Close your eyes and see the light\nPatterns only darkness writes\nPhosphene visions, neural bright\nThe mind its own display tonight\n\nPress and hold, the stars appear\nInner cosmos, crystal clear`,
  },
  {
    id: 4, title: 'Static Mind', album: 'Phantom OS', genre: 'Dark Ambient', bpm: 68,
    duration: '5:21', released: '2025-06', emoji: '📡',
    colorA: '#0a1a0a', colorB: '#34d399',
    story: 'A meditation on cognitive overload — layers of white noise slowly dissolving into pure silence.',
    lyrics: `Frequency overload\nSignals without source\nThe mind receives it all\nThen loses the discourse\n\nStatic, static, white and free\nSilence underneath the sea`,
  },
  {
    id: 5, title: 'Recursive Dream', album: 'Signal Paths', genre: 'Electronic', bpm: 140,
    duration: '3:17', released: '2026-01', emoji: '🔄',
    colorA: '#1a1500', colorB: '#fb923c',
    story: 'What happens when a dream dreams itself? Built entirely from samples of the first four tracks — a self-referential loop.',
    lyrics: `Dream within a dream within a dream\nRecursion, nothing\'s what it seems\nStack overflow of consciousness\nCall the self, address distress`,
  },
  {
    id: 6, title: 'Zero Gravity', album: 'Phantom OS', genre: 'Ambient', bpm: 75,
    duration: '4:44', released: '2025-03', emoji: '🪐',
    colorA: '#050a1a', colorB: '#7dd3fc',
    story: 'Composed after reading about gravitational waves — sound waves that bend spacetime itself.',
    lyrics: `Weightless in the void\nNo up, no down, no ground\nJust the hum of empty space\nThe loneliest of sound`,
  },
]

// ─── Design work ──────────────────────────────────────────────────────────────
export const designs = [
  { id: 1, title: 'Smart City Dashboard', category: 'UI Design', type: 'ui', tags: ['Dark UI', 'Data Viz'], year: '2026', colorA: '#0b1929', colorB: '#5b9cf6', tall: false },
  { id: 2, title: 'Daean Studio Identity', category: 'Branding', type: 'brand', tags: ['Logo', 'Typography'], year: '2025', colorA: '#140d1e', colorB: '#a78bfa', tall: true },
  { id: 3, title: 'Neon Glow Poster', category: 'Poster Design', type: 'poster', tags: ['Typographic', 'Print'], year: '2025', colorA: '#1a0f00', colorB: '#fb923c', tall: false },
  { id: 4, title: 'Dark OS Concept', category: 'UI Concept', type: 'concept', tags: ['macOS', 'Futuristic'], year: '2025', colorA: '#0a0a14', colorB: '#7dd3fc', tall: true },
  { id: 5, title: 'Type Study No.1', category: 'Typography', type: 'type', tags: ['Experimental', 'Print'], year: '2024', colorA: '#0d0d0d', colorB: '#f9fafb', tall: false },
  { id: 6, title: 'Game HUD System', category: 'UI/UX', type: 'hud', tags: ['Game UI', 'Figma'], year: '2024', colorA: '#001a0a', colorB: '#34d399', tall: true },
]

// ─── Games ────────────────────────────────────────────────────────────────────
export const games = [
  {
    id: 'mirror',
    title: 'Mirror World',
    genre: 'Puzzle / Multiplayer',
    desc: 'Real-time multiplayer puzzle game built with Unity WebGL. Players control mirrored avatars simultaneously to unlock spatial paradoxes.',
    techs: ['Unity', 'C#', 'Photon', 'WebGL'],
    rating: 4.7, reviews: 128,
    status: 'Released',
    colorA: '#050f1a', colorB: '#3b82f6', emoji: '🔮',
    year: '2025',
    trailer: 'A surreal multiplayer puzzle experience where your mirror-self works against — and with — you.',
  },
  {
    id: 'echoes',
    title: 'Echoes of Neon',
    genre: 'Action / Roguelite',
    desc: 'A procedurally generated neon-drenched roguelite where every run mutates the world rules. Built entirely in the browser with vanilla WebGL.',
    techs: ['WebGL', 'TypeScript', 'GLSL', 'Web Audio'],
    rating: 4.5, reviews: 84,
    status: 'Early Access',
    colorA: '#0a0014', colorB: '#a78bfa', emoji: '🌃',
    year: '2025',
    trailer: 'Every death changes the world. Every run rewrites the rules. How far can you push the neon?',
  },
  {
    id: 'void',
    title: 'Void Protocol',
    genre: 'Stealth / Pixel',
    desc: 'A pixel-art stealth infiltration game set in a decaying cyberpunk city. Navigate corporate server rooms, avoid AI guards, and extract the data.',
    techs: ['Godot 4', 'GDScript', 'Aseprite', 'FMOD'],
    rating: 4.3, reviews: 62,
    status: 'In Development',
    colorA: '#0a0a00', colorB: '#fb923c', emoji: '🕹️',
    year: '2026',
    trailer: 'Disappear. Extract. Survive. The AI never forgets a face.',
  },
]

// ─── Lab ──────────────────────────────────────────────────────────────────────
export const labItems = [
  { id: 1, icon: '🧠', title: 'Neural Style Engine', desc: 'Real-time style transfer in the browser using TensorFlow.js and a pre-trained VGG-19 backbone.', status: 'active', tags: ['TensorFlow.js', 'WebGL'] },
  { id: 2, icon: '🏙️', title: 'Procedural City Gen', desc: 'L-system + noise-based city layout generator. Produces unique road networks every render.', status: 'active', tags: ['Three.js', 'GLSL'] },
  { id: 3, icon: '🎵', title: 'Music Visualizer', desc: 'Web Audio API + WebGL oscilloscope / FFT visualizer that reacts to frequency bands in real time.', status: 'wip', tags: ['Web Audio', 'Canvas'] },
  { id: 4, icon: '✍️', title: 'Type Animator', desc: 'SVG-path-based kinetic typography engine. Define keyframe rules, the engine interpolates between letterforms.', status: 'wip', tags: ['SVG', 'GSAP'] },
  { id: 5, icon: '🌊', title: 'Particle Field', desc: 'GPU-accelerated particle system — 200k particles responding to mouse vector fields on a single canvas.', status: 'active', tags: ['WebGL', 'GLSL'] },
  { id: 6, icon: '🤖', title: 'AI Chat Persona', desc: 'Experimental persona engine that gives LLM responses a stable character identity across multi-turn sessions.', status: 'concept', tags: ['LangChain', 'GPT-4o'] },
]

// ─── About ────────────────────────────────────────────────────────────────────
export const timeline = [
  { date: '2026', title: 'AI Smart City Platform', org: 'SSAFY × NoAI Team', desc: 'Led frontend architecture and AI pipeline integration for a real-time urban monitoring system. WebSocket, Isolation Forest, Spring Boot.' },
  { date: '2025', title: 'Freelance Creative Dev', org: 'Independent', desc: 'Shipped 4 projects spanning interactive web experiences, game prototypes, and music production tools for independent clients.' },
  { date: '2024', title: 'Deep Dive into Full-Stack', org: 'Self-Directed', desc: 'Systematic study of backend architecture (Spring Boot, FastAPI), cloud infra (Docker, AWS), and real-time systems.' },
  { date: '2023', title: 'First Lines of Code', org: 'Self-Directed', desc: 'Built a personal blog and first Vue.js project. Fell in love with the intersection of design and systems thinking.' },
]

export const skills = [
  { name: 'Vue 3 / TypeScript', level: 92 },
  { name: 'Spring Boot / Java', level: 85 },
  { name: 'AI / ML Integration', level: 78 },
  { name: 'Creative Coding / GLSL', level: 70 },
  { name: 'Music Production', level: 75 },
]

export const techStack = [
  { emoji: '⚡', name: 'Vue 3' },
  { emoji: '🔷', name: 'TypeScript' },
  { emoji: '☕', name: 'Spring Boot' },
  { emoji: '🐍', name: 'FastAPI' },
  { emoji: '🐳', name: 'Docker' },
  { emoji: '🔴', name: 'Redis' },
  { emoji: '🗺️', name: 'Leaflet' },
  { emoji: '🎮', name: 'Unity' },
]
