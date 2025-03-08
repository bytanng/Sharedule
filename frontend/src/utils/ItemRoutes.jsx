import {
    API_URL,
    POST_METHOD,
    GET_METHOD,
    PUT_METHOD,
  } from "./Constants";

export const getItem = async (id) => {
    const token = localStorage.getItem("token");
  
    const response = await fetch(`${API_URL}/item/${id}`, {
      method: GET_METHOD,
      headers: {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
      },
      credentials: "include",
    });
  
    return await response.json();
};

export const editItem = async (id, itemData) => {
    try {
        const token = localStorage.getItem("token");
        if (!token) {
            throw new Error("No authentication token found");
        }

        const response = await fetch(`${API_URL}/item/${id}`, {
            method: PUT_METHOD,
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
            credentials: "include",
            body: JSON.stringify(itemData)
        });

        if (!response.ok) {
            const errorData = await response.text();
            throw new Error(errorData);
        }

        const data = await response.json();
        return { success: true, data }

    } catch (error) {
        return "Failed to edit listing";
    }
};

