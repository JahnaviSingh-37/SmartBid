# SmartBid Frontend Setup

## 🚀 Quick Start

### Prerequisites
- Node.js 18+ installed
- npm or yarn package manager

### Installation

1. **Navigate to frontend directory:**
```bash
cd frontend
```

2. **Install dependencies:**
```bash
npm install
```

3. **Install additional Tailwind dependencies:**
```bash
npm install -D @tailwindcss/forms @tailwindcss/typography @tailwindcss/aspect-ratio
```

4. **Install missing dependencies:**
```bash
npm install react-error-boundary
```

5. **Start development server:**
```bash
npm start
```

The application will open at `http://localhost:3000`

## 📁 Project Structure

```
frontend/
├── public/                  # Static assets
│   ├── index.html          # Main HTML template
│   ├── manifest.json       # PWA manifest
│   └── favicon.ico         # Favicon
│
├── src/
│   ├── components/         # Reusable components
│   │   ├── Layout/         # Header, Footer, etc.
│   │   ├── Auth/          # Login, Register forms
│   │   ├── Auction/       # Auction-related components
│   │   ├── UI/            # Generic UI components
│   │   └── Search/        # Search components
│   │
│   ├── pages/             # Page components
│   │   ├── HomePage.tsx
│   │   ├── AuctionsPage.tsx
│   │   ├── LoginPage.tsx
│   │   └── ...
│   │
│   ├── services/          # API services
│   │   ├── api.ts         # Axios configuration
│   │   ├── auth.ts        # Authentication API
│   │   ├── auctions.ts    # Auction API
│   │   └── websocket.ts   # WebSocket service
│   │
│   ├── hooks/             # Custom React hooks
│   │   ├── useAuth.ts
│   │   ├── useAuctions.ts
│   │   └── useWebSocket.ts
│   │
│   ├── context/           # React context providers
│   │   ├── AuthContext.tsx
│   │   ├── WebSocketContext.tsx
│   │   └── StripeContext.tsx
│   │
│   ├── types/             # TypeScript type definitions
│   │   ├── auth.types.ts
│   │   ├── auction.types.ts
│   │   └── api.types.ts
│   │
│   ├── utils/             # Utility functions
│   │   ├── formatters.ts
│   │   ├── validators.ts
│   │   └── constants.ts
│   │
│   ├── App.tsx            # Main App component
│   ├── index.tsx          # Entry point
│   └── index.css          # Global styles (Tailwind)
│
├── package.json           # Dependencies and scripts
├── tailwind.config.js     # Tailwind CSS configuration
├── craco.config.js        # CRACO configuration (path aliases)
└── tsconfig.json         # TypeScript configuration
```

## 🛠️ Development Commands

```bash
# Start development server
npm start

# Build for production
npm run build

# Run tests
npm test

# Lint code
npm run lint

# Fix lint issues
npm run lint:fix

# Format code
npm run format
```

## 🎨 Key Features

### Tailwind CSS Setup
- Custom color palette for SmartBid branding
- Responsive design utilities
- Custom component classes
- Dark mode support (ready)

### Component Architecture
- Modular component structure
- TypeScript for type safety
- Custom hooks for state management
- Context providers for global state

### Performance Optimizations
- Lazy loading of pages
- Image optimization
- Bundle splitting
- Service worker ready

### Real-time Features
- WebSocket integration for live bidding
- Real-time notifications
- Live auction updates

## 📱 Responsive Design

The frontend is fully responsive with breakpoints:
- `sm`: 640px and up
- `md`: 768px and up
- `lg`: 1024px and up
- `xl`: 1280px and up
- `2xl`: 1536px and up

## 🔐 Authentication Flow

1. User registration/login
2. JWT token storage
3. Protected route handling
4. Automatic token refresh
5. Secure logout

## 🎯 Next Steps

After installation, you should:

1. **Create the missing components:**
   - Start with basic page components
   - Add authentication forms
   - Build auction listing components

2. **Implement API services:**
   - Configure Axios for API calls
   - Add error handling
   - Implement caching

3. **Add state management:**
   - Complete React Context providers
   - Add custom hooks
   - Handle loading states

4. **Style components:**
   - Apply Tailwind CSS classes
   - Add animations
   - Ensure accessibility

## 🚀 Ready to Code!

Your frontend structure is ready for development. Follow the component-driven approach and build features incrementally.

Start with the HomePage component and gradually add more functionality!
