import {
  API_URL,
  POST_METHOD,
  GET_METHOD,
  DELETE_METHOD,
  PUT_METHOD,
} from "./Constants";

export const createTimeslot = async (timeslotData, transactionId) => {
  try {
    const token = localStorage.getItem("token");
    if (!token) {
      throw new Error("No authentication token found");
    }

    const response = await fetch(
      `${API_URL}/create-timeslot/${transactionId}`,
      {
        method: POST_METHOD,
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(timeslotData),
      }
    );

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
      error: error.message || "Failed to create timeslot",
    };
  }
};

export const getTimeslotByTransaction = async (transactionId) => {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error("No authentication token found");
  }

  const response = await fetch(`${API_URL}/timeslot/${transactionId}`, {
    method: GET_METHOD,
    headers: {
      Authorization: `Bearer ${token}`,
      "Content-Type": "application/json",
    },
  });

  const data = await response.json();

  console.log(data);

  return data;
};
