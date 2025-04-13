import {
  API_URL,
  POST_METHOD,
  GET_METHOD,
  DELETE_METHOD,
  PUT_METHOD,
} from "./Constants";

export const createTransaction = async (id, transactionData) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No authentication token found");
    }

    const response = await fetch(`${API_URL}/create-transaction/${id}`, {
      method: POST_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify(transactionData),
    });

    if (!response.ok) {
      const errorData = await response.text();
      throw new Error(errorData);
    }

    const data = await response.json();

    return {
      success: true,
      data,
    };
  } catch (error) {
    return {
      success: false,
      error: error.message || "Failed to create transaction",
    };
  }
};

export const getUserTransactions = async (id) => {
  const token = localStorage.getItem("token"); // Retrieve token from localStorage

  if (!token) {
    console.error("No authentication token found");
    return null;
  }

  try {
    const response = await fetch(`${API_URL}/transactions/${id}`, {
      method: GET_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`Error: ${response.status} - ${await response.text()}`);
    }

    return await response.json();
  } catch (error) {
    console.error("Failed to fetch user transactions:", error.message);
    return [];  // Return empty array instead of null to avoid errors
  }
};

export const bookTransaction = async (transactionId) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No authentication token found");
    }

    const response = await fetch(`${API_URL}/transactions/book`, {
      method: POST_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ transactionId }),
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || "Failed to book transaction");
    }

    const data = await response.json();
    return {
      success: true,
      data,
    };
  } catch (error) {
    return {
      success: false,
      error: error.message || "Failed to book transaction",
    };
  }
};
