# SmartBid Frontend Setup Instructions

## Prerequisites
- Node.js 18+ and npm
- Internet connection for dependency installation

## Installation Steps

1. **Navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Start development server:**
   ```bash
   npm start
   ```

4. **Build for production:**
   ```bash
   npm run build
   ```

## Resolving TypeScript Errors

The current TypeScript compilation errors are expected and will be resolved once dependencies are installed:

- **React & React-Router**: Required for JSX components and routing
- **Axios**: For API client functionality  
- **Tailwind CSS**: For styling system
- **TypeScript**: For type definitions

After running `npm install`, all compilation errors should be resolved.

## Environment Variables

Create a `.env` file in the frontend directory with:

```env
REACT_APP_API_URL=http://localhost:8080/api
REACT_APP_WS_URL=ws://localhost:8080/ws
REACT_APP_STRIPE_PUBLISHABLE_KEY=your_stripe_key_here
```

## Development Workflow

1. **Start backend server** (Port 8080)
2. **Start frontend development server** (Port 3000)
3. **Access application** at http://localhost:3000

## Available Scripts

- `npm start` - Development server with hot reload
- `npm run build` - Production build
- `npm test` - Run test suite
- `npm run lint` - ESLint code checking
- `npm run format` - Prettier code formatting

## Project Structure

The frontend is organized as follows:

```
frontend/
├── public/                 # Static assets
├── src/
│   ├── components/        # Reusable UI components
│   │   └── Layout/       # Header, Footer, Layout
│   ├── pages/            # Page components
│   ├── services/         # API services
│   ├── context/          # React context providers
│   ├── hooks/            # Custom React hooks
│   ├── types/            # TypeScript type definitions
│   ├── utils/            # Utility functions
│   ├── App.tsx           # Main app component
│   └── index.tsx         # App entry point
├── tailwind.config.js    # Tailwind CSS configuration
└── package.json          # Dependencies and scripts
```

## Network Issues

If you encounter network connectivity issues during `npm install`:

1. **Check internet connection**
2. **Try different registry:**
   ```bash
   npm config set registry https://registry.npmjs.org/
   ```
3. **Clear npm cache:**
   ```bash
   npm cache clean --force
   ```
4. **Use yarn as alternative:**
   ```bash
   yarn install
   ```

## Next Steps

Once dependencies are installed:
1. Complete component implementations
2. Add form validation and error handling
3. Integrate with backend API
4. Implement real-time WebSocket features
5. Add comprehensive testing

The frontend structure is complete and ready for development once dependencies are resolved.
