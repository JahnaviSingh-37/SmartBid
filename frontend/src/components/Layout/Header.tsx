import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { 
  Bars3Icon, 
  XMarkIcon,
  MagnifyingGlassIcon,
  BellIcon,
  UserCircleIcon,
  ShoppingBagIcon,
  HeartIcon,
  CogIcon
} from '@heroicons/react/24/outline';
import { Menu, Transition } from '@headlessui/react';

import { useAuth } from '../../context/AuthContext';
import { formatCurrency } from '../../utils/formatters';
import Logo from './Logo';

// Simple placeholder components
const SearchBar = () => (
  <div className="max-w-lg flex-1 md:max-w-xs">
    <div className="relative">
      <div className="absolute inset-y-0 left-0 pl-3 flex items-center">
        <MagnifyingGlassIcon className="h-5 w-5 text-gray-400" />
      </div>
      <input
        className="block w-full pl-10 pr-3 py-2 border border-gray-300 rounded-md leading-5 bg-white placeholder-gray-500 focus:outline-none focus:placeholder-gray-400 focus:ring-1 focus:ring-primary-500 focus:border-primary-500 text-sm"
        placeholder="Search auctions..."
        type="search"
      />
    </div>
  </div>
);

const NotificationDropdown = () => (
  <button className="p-1 rounded-full text-gray-400 hover:text-gray-500">
    <BellIcon className="h-6 w-6" />
  </button>
);

interface HeaderProps {
  className?: string;
}

const Header: React.FC<HeaderProps> = ({ className = '' }) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const { user, logout, isAuthenticated } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = async () => {
    await logout();
    navigate('/');
  };

  const navigation = [
    { name: 'Home', href: '/', current: location.pathname === '/' },
    { name: 'Auctions', href: '/auctions', current: location.pathname === '/auctions' },
    { name: 'Categories', href: '/categories', current: location.pathname === '/categories' },
    { name: 'How it Works', href: '/how-it-works', current: location.pathname === '/how-it-works' },
  ];

  const userNavigation = [
    { name: 'My Profile', href: '/profile', icon: UserCircleIcon },
    { name: 'My Bids', href: '/my-bids', icon: ShoppingBagIcon },
    { name: 'Watchlist', href: '/watchlist', icon: HeartIcon },
    { name: 'Settings', href: '/settings', icon: CogIcon },
  ];

  return (
    <header className={`bg-white shadow-sm border-b border-gray-200 sticky top-0 z-50 ${className}`}>
      <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
        <div className="flex h-16 justify-between items-center">
          {/* Logo and Main Navigation */}
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <Link to="/" className="flex items-center space-x-2">
                <Logo className="h-8 w-auto" />
                <span className="hidden sm:block text-xl font-bold gradient-text">
                  SmartBid
                </span>
              </Link>
            </div>
            
            {/* Desktop Navigation */}
            <nav className="hidden md:ml-8 md:flex md:space-x-8">
              {navigation.map((item) => (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`inline-flex items-center px-1 pt-1 text-sm font-medium transition-colors ${
                    item.current
                      ? 'text-primary-600 border-b-2 border-primary-600'
                      : 'text-gray-500 hover:text-gray-700 hover:border-b-2 hover:border-gray-300'
                  }`}
                >
                  {item.name}
                </Link>
              ))}
            </nav>
          </div>

          {/* Search Bar */}
          <div className="hidden lg:block flex-1 max-w-lg mx-8">
            <SearchBar />
          </div>

          {/* Right side */}
          <div className="flex items-center space-x-4">
            {/* Mobile search button */}
            <button
              type="button"
              className="lg:hidden p-2 text-gray-400 hover:text-gray-500"
              onClick={() => {/* Handle mobile search */}}
            >
              <MagnifyingGlassIcon className="h-6 w-6" />
            </button>

            {isAuthenticated ? (
              <>
                {/* Credit Score Display */}
                {user?.creditScore && (
                  <div className="hidden sm:flex items-center space-x-2 bg-gray-50 rounded-lg px-3 py-2">
                    <span className="text-xs font-medium text-gray-600">Credit Score:</span>
                    <span className={`text-sm font-bold ${
                      user.creditScore >= 800 ? 'text-success-600' :
                      user.creditScore >= 700 ? 'text-primary-600' :
                      user.creditScore >= 600 ? 'text-warning-600' :
                      'text-danger-600'
                    }`}>
                      {user.creditScore.toFixed(0)}
                    </span>
                  </div>
                )}

                {/* Notifications */}
                <NotificationDropdown />

                {/* Create Auction Button */}
                <Link
                  to="/create-auction"
                  className="hidden sm:inline-flex btn-primary btn-sm"
                >
                  Sell Item
                </Link>

                {/* User Profile Dropdown */}
                <Menu as="div" className="relative">
                  <Menu.Button className="flex items-center space-x-2 text-sm rounded-full focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2">
                    <div className="flex items-center space-x-2">
                      {user?.profileImageUrl ? (
                        <img
                          className="h-8 w-8 rounded-full object-cover"
                          src={user.profileImageUrl}
                          alt={user.firstName}
                        />
                      ) : (
                        <UserCircleIcon className="h-8 w-8 text-gray-400" />
                      )}
                      <span className="hidden sm:block font-medium text-gray-700">
                        {user?.firstName}
                      </span>
                    </div>
                  </Menu.Button>

                  <Transition
                    enter="transition ease-out duration-200"
                    enterFrom="transform opacity-0 scale-95"
                    enterTo="transform opacity-100 scale-100"
                    leave="transition ease-in duration-75"
                    leaveFrom="transform opacity-100 scale-100"
                    leaveTo="transform opacity-0 scale-95"
                  >
                    <Menu.Items className="absolute right-0 z-10 mt-2 w-56 origin-top-right bg-white rounded-md shadow-lg ring-1 ring-black ring-opacity-5 focus:outline-none">
                      <div className="px-4 py-3 border-b border-gray-200">
                        <p className="text-sm font-medium text-gray-900">
                          {user?.firstName} {user?.lastName}
                        </p>
                        <p className="text-sm text-gray-500 truncate">
                          {user?.email}
                        </p>
                      </div>
                      
                      <div className="py-1">
                        {userNavigation.map((item) => (
                          <Menu.Item key={item.name}>
                            {({ active }) => (
                              <Link
                                to={item.href}
                                className={`flex items-center px-4 py-2 text-sm ${
                                  active
                                    ? 'bg-gray-100 text-gray-900'
                                    : 'text-gray-700'
                                }`}
                              >
                                <item.icon className="h-4 w-4 mr-3" />
                                {item.name}
                              </Link>
                            )}
                          </Menu.Item>
                        ))}
                      </div>
                      
                      <div className="border-t border-gray-200 py-1">
                        <Menu.Item>
                          {({ active }) => (
                            <button
                              onClick={handleLogout}
                              className={`flex w-full items-center px-4 py-2 text-sm ${
                                active
                                  ? 'bg-gray-100 text-gray-900'
                                  : 'text-gray-700'
                              }`}
                            >
                              Sign out
                            </button>
                          )}
                        </Menu.Item>
                      </div>
                    </Menu.Items>
                  </Transition>
                </Menu>
              </>
            ) : (
              /* Not authenticated */
              <div className="flex items-center space-x-4">
                <Link
                  to="/login"
                  className="text-gray-600 hover:text-gray-900 font-medium"
                >
                  Sign in
                </Link>
                <Link
                  to="/register"
                  className="btn-primary btn-sm"
                >
                  Sign up
                </Link>
              </div>
            )}

            {/* Mobile menu button */}
            <button
              type="button"
              className="md:hidden p-2 text-gray-400 hover:text-gray-500"
              onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
            >
              {isMobileMenuOpen ? (
                <XMarkIcon className="h-6 w-6" />
              ) : (
                <Bars3Icon className="h-6 w-6" />
              )}
            </button>
          </div>
        </div>

        {/* Mobile menu */}
        <Transition
          show={isMobileMenuOpen}
          enter="transition ease-out duration-200"
          enterFrom="opacity-0 -translate-y-1"
          enterTo="opacity-100 translate-y-0"
          leave="transition ease-in duration-150"
          leaveFrom="opacity-100 translate-y-0"
          leaveTo="opacity-0 -translate-y-1"
        >
          <div className="md:hidden border-t border-gray-200 pt-4 pb-3">
            <div className="space-y-1">
              {navigation.map((item) => (
                <Link
                  key={item.name}
                  to={item.href}
                  className={`block px-3 py-2 text-base font-medium ${
                    item.current
                      ? 'text-primary-600 bg-primary-50'
                      : 'text-gray-600 hover:text-gray-900 hover:bg-gray-50'
                  }`}
                  onClick={() => setIsMobileMenuOpen(false)}
                >
                  {item.name}
                </Link>
              ))}
            </div>
            
            {/* Mobile search */}
            <div className="mt-4 px-3">
              <SearchBar />
            </div>
            
            {isAuthenticated && (
              <>
                <div className="mt-4 pt-4 border-t border-gray-200">
                  {userNavigation.map((item) => (
                    <Link
                      key={item.name}
                      to={item.href}
                      className="flex items-center px-3 py-2 text-base font-medium text-gray-600 hover:text-gray-900 hover:bg-gray-50"
                      onClick={() => setIsMobileMenuOpen(false)}
                    >
                      <item.icon className="h-5 w-5 mr-3" />
                      {item.name}
                    </Link>
                  ))}
                </div>
                
                <div className="mt-4 px-3">
                  <Link
                    to="/create-auction"
                    className="btn-primary btn-md w-full justify-center"
                    onClick={() => setIsMobileMenuOpen(false)}
                  >
                    Sell Item
                  </Link>
                </div>
              </>
            )}
          </div>
        </Transition>
      </div>
    </header>
  );
};

export default Header;
