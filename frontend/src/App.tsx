import React, { Suspense } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Layout from './components/Layout/Layout';
import ProtectedRoute from './components/ProtectedRoute';

// Lazy load pages for better performance
const HomePage = React.lazy(() => import('./pages/HomePage'));
const LoginPage = React.lazy(() => import('./pages/LoginPage'));
const RegisterPage = React.lazy(() => import('./pages/RegisterPage'));
const AuctionsPage = React.lazy(() => import('./pages/AuctionsPage'));
const AuctionDetailPage = React.lazy(() => import('./pages/AuctionDetailPage'));
const CreateAuctionPage = React.lazy(() => import('./pages/CreateAuctionPage'));
const ProfilePage = React.lazy(() => import('./pages/ProfilePage'));
const WatchlistPage = React.lazy(() => import('./pages/WatchlistPage'));
const MyBidsPage = React.lazy(() => import('./pages/MyBidsPage'));

// Simple loading component
const LoadingSpinner = () => (
  <div className="flex justify-center items-center h-64">
    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-600"></div>
  </div>
);

// Simple error fallback component
const ErrorFallback = ({ error }: { error: Error }) => (
  <div className="flex flex-col items-center justify-center h-64 text-center">
    <h2 className="text-xl font-semibold text-gray-900 mb-2">Something went wrong</h2>
    <p className="text-gray-600">{error.message}</p>
    <button
      onClick={() => window.location.reload()}
      className="mt-4 px-4 py-2 bg-primary-600 text-white rounded hover:bg-primary-700"
    >
      Reload page
    </button>
  </div>
);

function App() {
  return (
    <Router>
      <AuthProvider>
        <Layout>
          <Suspense fallback={<LoadingSpinner />}>
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<HomePage />} />
              <Route path="/auctions" element={<AuctionsPage />} />
              <Route path="/auctions/:id" element={<AuctionDetailPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />
              
              {/* Protected Routes */}
              <Route 
                path="/profile" 
                element={
                  <ProtectedRoute>
                    <ProfilePage />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/create-auction" 
                element={
                  <ProtectedRoute>
                    <CreateAuctionPage />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/my-bids" 
                element={
                  <ProtectedRoute>
                    <MyBidsPage />
                  </ProtectedRoute>
                } 
              />
              <Route 
                path="/watchlist" 
                element={
                  <ProtectedRoute>
                    <WatchlistPage />
                  </ProtectedRoute>
                } 
              />
              
              {/* 404 Route */}
              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Suspense>
        </Layout>
      </AuthProvider>
    </Router>
  );
}

export default App;
