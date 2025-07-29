import React, { useState, useEffect } from 'react';
import { Link } from 'react-router';
import { Search as SearchIcon } from 'lucide-react';
import type { User } from '@/types/types';
import Navbar from '@/components/navbar';
import { search } from '@/services/user';

const Search: React.FC = () => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [query, setQuery] = useState('');
  const [results, setResults] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [debouncedQuery, setDebouncedQuery] = useState('');

  // Debounce search query
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedQuery(query);
    }, 500);

    return () => clearTimeout(timer);
  }, [query]);

  // Search users when debounced query changes
  useEffect(() => {
    const searchUsers = async () => {
      if (!debouncedQuery.trim()) {
        setResults([]);
        return;
      }

      setLoading(true);
      try {
        const response = await search(debouncedQuery);
        setResults(response.result || []);
      } catch (error) {
        console.error(error);
        setResults([]);
      } finally {
        setLoading(false);
      }
    };

    searchUsers();
  }, [debouncedQuery]);

  return (
    <div className="flex min-h-screen">
      {/* Navbar: 1/5 màn hình */}
      <div className="w-1/5">
        <Navbar onCreateClick={() => setIsDialogOpen(true)} />
      </div>

      {/* Content area: 4/5 màn hình còn lại */}
      <div className="flex-1 p-6">
        <div className="max-w-xl mx-auto">
          <h1 className="text-xl font-semibold mb-6">Search</h1>

          <div className="relative mb-6">
            <div className="absolute inset-y-0 left-3 flex items-center pointer-events-none">
              <SearchIcon size={18} className="text-instagram-darkGray" />
            </div>
            <input
              type="text"
              placeholder="Search users"
              className="w-full bg-gray-100 border border-transparent rounded-lg pl-10 py-2 focus:outline-none focus:border-instagram-border"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
            />
          </div>

          {loading ? (
            <div className="flex justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-t-2 border-b-2 border-instagram-blue"></div>
            </div>
          ) : (
            <div>
              {query && results.length === 0 ? (
                <div className="text-center py-8">
                  <p className="text-instagram-darkGray">No users found</p>
                </div>
              ) : results.length > 0 ? (
                <div className="space-y-3 border border-gray-200 rounded-md p-4">
                  {results.map(user => (
                    <Link
                      key={user.id}
                      to="/profile" // Sử dụng route tĩnh để tránh lộ id
                      state={{ userId: user.id }} // Truyền userId qua state
                      className="flex items-center p-3 hover:bg-gray-50 rounded-md transition-colors"
                    >
                      <img
                        src={user.avatarUrl || "assets/unknown.png"}
                        alt={user.fullName}
                        className="w-12 h-12 rounded-full object-cover mr-3"
                      />
                      <div>
                        <div className="font-semibold">{user.fullName}</div>
                        {/* <div className="text-instagram-darkGray text-sm">{user.fullName}</div> */}
                      </div>
                    </Link>
                  ))}
                </div>
              ) : null}
            </div>
          )}

          {!query && (
            <div className="text-center py-10">
              <SearchIcon size={48} className="mx-auto text-instagram-darkGray mb-4" />
              <h2 className="text-xl font-semibold mb-2">Search for users</h2>
              <p className="text-instagram-darkGray">
                Search for users by username or name.
              </p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Search;