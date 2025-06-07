import React from 'react';

const Footer = () => {
  return (
    <footer className="bg-gray-800 text-white p-4 mt-8">
      <div className="container mx-auto text-center text-sm">
        &copy; {new Date().getFullYear()} NewsAggregator. All rights reserved.
      </div>
    </footer>
  );
};

export default Footer;