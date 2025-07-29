import React, { useEffect, useRef, useState, useCallback } from "react";
import Navbar from "@/components/navbar";
import type { Conversation, Message, User } from "@/types/types";
import { search } from "@/services/user";
import { createConversation, createMessage, getConversations, getMessages } from "@/services/chat";
import { isAuthenticated } from "@/services/auth";
import { useNavigate } from "react-router";
import { getToken, getUser } from "@/services/storage";
import type { ListChatMessageResponse, ListConversationResponse } from "@/types/chat";
import type { SearchUserResponse } from "@/types/user";
import { io } from "socket.io-client";
import type { Socket } from "socket.io-client";

const Chat: React.FC = () => {
  const [user, setUser] = useState<User | null>(null);
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [messagesMap, setMessagesMap] = useState<{ [key: string]: Message[] }>({});
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [inputMessage, setInputMessage] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const selectedConversationRef = useRef<string | null>(null);
  const [searchResults, setSearchResults] = useState<User[]>([]);

  const messagesEndRef = useRef<HTMLDivElement>(null);
  const socketRef = useRef<Socket | null>(null);
  const navigate = useNavigate();

  // Scroll to bottom of message list
  const scrollToBottom = useCallback(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollTop = messagesEndRef.current.scrollHeight;
      setTimeout(() => {
        if (messagesEndRef.current) {
          messagesEndRef.current.scrollTop = messagesEndRef.current.scrollHeight;
        }
      }, 100);
      setTimeout(() => {
        if (messagesEndRef.current) {
          messagesEndRef.current.scrollTop = messagesEndRef.current.scrollHeight;
        }
      }, 300);
    }
  }, []);

  // Check authentication and fetch user info
  useEffect(() => {
    if (!isAuthenticated()) {
      navigate("/login");
    } else {
      const userData = getUser();
      if (userData) {
        setUser(JSON.parse(userData));
      }
    }
  }, [navigate]);

  // Initialize WebSocket connection
  useEffect(() => {
    if (!socketRef.current) {
      const token = getToken();
      socketRef.current = io(`http://localhost:8182?token=${token}`);

      socketRef.current.on("connect", () => {
        console.log("Socket connected:", socketRef.current?.id);
        socketRef.current?.emit("register-session", {
          clientSessionId: socketRef.current?.id,
        });
      });

      socketRef.current.on("disconnect", () => {
        console.log("Socket disconnected");
      });

      socketRef.current.on("message", (message) => {
        const messageObject = JSON.parse(message);
        if(messageObject?.conversationId){
          handleIncomingMessage(messageObject);
        }
      });
    }

    return () => {
      if (socketRef.current) {
        socketRef.current.disconnect();
        socketRef.current = null;
      }
    };
  }, []);

  // Handle incoming WebSocket messages
  const handleIncomingMessage = useCallback((message: Message) => {
    setMessagesMap((prev) => {
      const existingMessages = prev[message.conversationId] || [];
      if (existingMessages.some((msg) => msg.id === message.id)) {
        return prev;
      }

      const updatedMessages = [...existingMessages, message].sort(
        (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
      );
      
      return {
        ...prev,
        [message.conversationId]: [...updatedMessages],
      };
    });

    setConversations((prevConversations) =>
      prevConversations.map((conv) =>
        conv.id === message.conversationId
          ? {
              ...conv,
              lastMessage: message.message,
              lastTimestamp: new Date(message.createdAt).toLocaleString(),
              // unread: selectedConversationRef.current === message.conversationId ? 0 : (conv.unread || 0) + 1,
            }
          : conv
      )
    );
  }, [messagesMap]);

  // Fetch conversations
  useEffect(() => {
    const fetchConversations = async () => {
      if (!isAuthenticated() || !user) {
        return;
      }

      try {
        const response: ListConversationResponse = await getConversations(user.id);
        setConversations(response.result || []);
      } catch (err) {
        console.error("Error fetching conversations:", err);
        setConversations([]);
      }
    };

    fetchConversations();
  }, [user]);

  // Select first conversation when available
  useEffect(() => {
    if (conversations.length > 0 && !selectedConversationRef.current) {
      selectedConversationRef.current = conversations[0].id;
    }
  }, [conversations]);

  // Fetch messages for selected conversation
  useEffect(() => {
    const fetchMessages = async (conversationId: string) => {
      if (conversationId && user && !messagesMap[conversationId]) {
        try {
          const response: ListChatMessageResponse = await getMessages(conversationId);
          const sortedMessages = response.result.sort(
            (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
          );
          setMessagesMap((prev) => ({
            ...prev,
            [conversationId]: sortedMessages,
          }));
        } catch (err) {
          setMessagesMap((prev) => ({
            ...prev,
            [conversationId]: [],
          }));
        }
      }
    };

    if (selectedConversationRef.current) {
      fetchMessages(selectedConversationRef.current);
    }
  }, [selectedConversationRef.current, user, messagesMap]);

  // Scroll to bottom when messages or conversation change
  useEffect(() => {
    scrollToBottom();
  }, [selectedConversationRef.current, messagesMap, scrollToBottom]);

  // Current messages for selected conversation
  const currentMessages = selectedConversationRef.current
    ? messagesMap[selectedConversationRef.current] || []
    : [];

  const handleSearchChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value;
    setSearchQuery(query);

    if (query.trim()) {
      try {
        const response: SearchUserResponse = await search(query);
        setSearchResults(response.result || []);
        setIsDropdownOpen(true);
      } catch (err) {
        console.error("Error searching users:", err);
        setSearchResults([]);
      }
    } else {
      setSearchResults([]);
      setIsDropdownOpen(false);
    }
  };

  const handleUserSelect = async (selectedUser: User) => {
    if (!user) return;

    const existingConversation = conversations.find(
      (conv) => conv.participant.id === selectedUser.id
    );

    if (existingConversation) {
      selectedConversationRef.current = existingConversation.id;
    } else {
      try {
        const newConversation = await createConversation(user.id, selectedUser.id);
        setConversations([...conversations, newConversation.result]);
        selectedConversationRef.current = newConversation.result.id;
      } catch (err) {
        console.error("Error creating conversation:", err);
      }
    }

    setSearchQuery("");
    setSearchResults([]);
    setIsDropdownOpen(false);
  };

  const handleSendMessage = async () => {
    if (inputMessage.trim() && selectedConversationRef.current && user) {
      try {
        const newMessage = await createMessage(selectedConversationRef.current, inputMessage);
        setMessagesMap((prev) => {
          if (!selectedConversationRef.current) return prev;
          const existingMessages = prev[selectedConversationRef.current] || [];
          if (existingMessages.some((msg) => msg.id === newMessage.result.id)) {
            return prev;
          }
          const updatedMessages = [...existingMessages, newMessage.result].sort(
            (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
          );
          return {
            ...prev,
            [selectedConversationRef.current]: updatedMessages,
          };
        });
        setConversations((prevConversations) =>
          prevConversations.map((conv) =>
            conv.id === selectedConversationRef.current
              ? {
                  ...conv,
                  lastMessage: newMessage.result.message,
                  lastTimestamp: new Date(newMessage.result.createdAt).toLocaleString(),
                  unread: 0,
                }
              : conv
          )
        );
        setInputMessage("");
      } catch (err) {
        console.error("Error sending message:", err);
      }
    }
  };

  const handleConversationClick = (id: string) => {
    selectedConversationRef.current = id;
    setConversations((prevConversations) =>
      prevConversations.map((conv) =>
        conv.id === id ? { ...conv, unread: 0 } : conv
      )
    );
  };

  return (
    <div className="flex min-h-screen">
      <div className="w-65 fixed bg-white border-r border-gray-200">
        <Navbar onCreateClick={() => setIsDialogOpen(true)} />
      </div>

      <div className="ml-65 w-1/4 bg-white p-4 shadow-md">
        <h2 className="text-lg font-semibold mb-4">{user?.fullName}</h2>
        <div className="flex flex-col justify-center">
          <div className="m-1 p-3 bg-gray-300 rounded-md relative">
            <input
              className="w-full outline-none"
              type="text"
              placeholder="Tìm kiếm"
              value={searchQuery}
              onChange={handleSearchChange}
              onFocus={() => setIsDropdownOpen(true)}
              onBlur={() => setTimeout(() => setIsDropdownOpen(false), 200)}
            />
            {isDropdownOpen && searchQuery && (
              <div className="absolute top-full left-0 w-full bg-white border border-gray-300 rounded-md mt-1 max-h-40 overflow-y-auto z-10">
                {searchResults.length > 0 ? (
                  searchResults.map((user) => (
                    <div
                      key={user.id}
                      className="p-2 hover:bg-gray-100 cursor-pointer"
                      onClick={() => handleUserSelect(user)}
                    >
                      <div className="flex items-center space-x-3">
                        <img
                          src={user.avatarUrl || "/assets/unknown.png"}
                          alt={user.fullName}
                          className="rounded-full w-14 h-14"
                        />
                        <div>
                          <p className="font-semibold">{user.fullName}</p>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="p-2 text-gray-500">Không tìm thấy người dùng.</p>
                )}
              </div>
            )}
          </div>

          <div className="m-1 overflow-y-auto max-h-96">
            {conversations.map((conv) => (
              <div
                key={conv.id}
                className="p-2 hover:bg-gray-300 cursor-pointer"
                onClick={() => handleConversationClick(conv.id)}
              >
                <div className="flex items-center space-x-3">
                  <img
                    src={conv.participant.avatarUrl || "/assets/unknown.png"}
                    alt={conv.participant.fullName}
                    className="rounded-full w-14 h-14"
                  />
                  <div>
                    <p className="font-semibold">{conv.participant.fullName}</p>
                    {/* <p className="text-sm text-gray-500">{conv.lastMessage}</p>
                    {conv.unread > 0 && (
                      <span className="bg-blue-500 text-white rounded-full px-2 py-1 text-xs">
                        {conv.unread}
                      </span>
                    )} */}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="w-3/4 p-4 bg-white shadow-md flex flex-col">
        <h2 className="text-lg font-semibold mb-4">
          {selectedConversationRef.current
            ? conversations.find((conv) => conv.id === selectedConversationRef.current)?.participant
                .fullName || "Tin nhắn"
            : "Tin nhắn"}
        </h2>

       <div
          className="flex-1 overflow-y-auto mb-4 space-y-4 max-h-[calc(100vh-130px)]"
          ref={messagesEndRef}
          style={{ scrollBehavior: "smooth" }}
        >
          {selectedConversationRef.current ? (
            currentMessages.length > 0 ? (
              <>
                {currentMessages.map((msg: Message, index) => (
                  <div
                    key={msg.id || index}
                    className={`flex ${msg.sender ? "justify-end" : "justify-start"}`}
                  >
                    <div
                      className={`flex items-start space-x-3 ${
                        msg.sender ? "flex-row-reverse space-x-reverse" : ""
                      }`}
                    >
                      <img
                        src={
                          msg.sender
                            ? user?.avatarUrl || "/assets/unknown.png"
                            : conversations
                                .find((conv) => conv.id === selectedConversationRef.current)
                                ?.participant.avatarUrl || "/assets/unknown.png"
                        }
                        alt="User"
                        className="w-8 h-8 rounded-full"
                      />
                      <p
                        className={`p-2 rounded-lg max-w-xs ${
                          msg.sender ? "bg-blue-500 text-white" : "bg-gray-100"
                        }`}
                      >
                        {msg.message}
                      </p>
                    </div>
                  </div>
                ))}
              </>
            ) : (
              <p className="text-gray-500 text-center">Hãy bắt đầu cuộc trò chuyện của bạn</p>
            )
          ) : (
            <p className="text-gray-500 text-center">Vui lòng chọn một cuộc trò chuyện.</p>
          )}
        </div>

        <div className="flex items-center space-x-2">
          <input
            type="text"
            value={inputMessage}
            onChange={(e) => setInputMessage(e.target.value)}
            className="flex-1 p-2 border rounded-lg"
            placeholder="Nhắn tin..."
            disabled={selectedConversationRef.current === null}
          />
          <button
            onClick={handleSendMessage}
            className="bg-blue-500 text-white p-2 rounded-lg"
            disabled={selectedConversationRef.current === null}
          >
            Gửi tin nhắn
          </button>
        </div>
        {isDialogOpen && (
          <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center">
            <div className="bg-white p-4 rounded-lg">
              <h3>Dialog</h3>
              <button onClick={() => setIsDialogOpen(false)}>Close</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Chat;