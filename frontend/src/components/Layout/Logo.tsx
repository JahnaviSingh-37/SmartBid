import React from 'react';

interface LogoProps {
  className?: string;
  variant?: 'full' | 'icon';
}

const Logo: React.FC<LogoProps> = ({ className = 'h-8 w-auto', variant = 'full' }) => {
  return (
    <div className={`${className} flex items-center`}>
      {/* Logo SVG */}
      <svg 
        viewBox="0 0 32 32" 
        className="h-full w-auto"
        fill="none" 
        xmlns="http://www.w3.org/2000/svg"
      >
        {/* Auction Hammer Icon */}
        <path
          d="M8 24L24 8M8 8L24 24"
          stroke="url(#gradient)"
          strokeWidth="2"
          strokeLinecap="round"
        />
        <circle
          cx="16"
          cy="16"
          r="14"
          stroke="url(#gradient)"
          strokeWidth="2"
          fill="none"
        />
        <defs>
          <linearGradient id="gradient" x1="0%" y1="0%" x2="100%" y2="100%">
            <stop offset="0%" stopColor="#3B82F6" />
            <stop offset="100%" stopColor="#1E40AF" />
          </linearGradient>
        </defs>
      </svg>
      
      {variant === 'full' && (
        <span className="ml-2 text-xl font-bold text-gray-900">
          Smart<span className="text-primary-600">Bid</span>
        </span>
      )}
    </div>
  );
};

export default Logo;
