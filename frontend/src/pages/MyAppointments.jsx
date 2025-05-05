import React, { useEffect, useState } from "react";
import { Footer, Navbar } from "../components";
import { API_URL } from "../utils/Constants";
import dayjs from "dayjs";
import { toast, ToastContainer } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";

const MyAppointments = () => {
  const [buyingAppointments, setBuyingAppointments] = useState([]);
  const [sellingAppointments, setSellingAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState("buying");

  useEffect(() => {
    const fetchAppointments = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("token");
        if (!token) {
          setError("You must be logged in to view your appointments");
          setLoading(false);
          return;
        }

        const response = await fetch(`${API_URL}/user/appointments`, {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json"
          }
        });

        if (!response.ok) {
          throw new Error(`Error: ${response.status} - ${await response.text()}`);
        }

        const data = await response.json();
        console.log("API response:", data);
        
        // Process buying appointments - ensure they have valid buyer and seller usernames
        const validBuyingAppointments = (data.buying || []).filter(apt => 
          apt.seller?.username && apt.buyer?.username
        );
        
        // Process selling appointments - ensure they have valid buyer username
        // and don't overlap with buying appointments (using transaction ID)
        const buyingIds = new Set(validBuyingAppointments.map(apt => apt.id));
        const validSellingAppointments = (data.selling || []).filter(apt => 
          apt.buyer?.username && !buyingIds.has(apt.id)
        );
        
        setBuyingAppointments(validBuyingAppointments);
        setSellingAppointments(validSellingAppointments);
      } catch (error) {
        console.error("Error fetching appointments:", error);
        setError("Failed to load your appointments. Please try again later.");
        toast.error("Failed to load your appointments");
      } finally {
        setLoading(false);
      }
    };

    fetchAppointments();
  }, []);

  const formatDateTime = (isoString) => {
    if (!isoString) return "Not specified";
    return dayjs(isoString).format("DD/MM/YYYY, HH:mm");
  };

  const formatTimeOnly = (isoString) => {
    if (!isoString) return "";
    return dayjs(isoString).format("HH:mm");
  };

  // Function to log appointment data for debugging
  useEffect(() => {
    if (buyingAppointments.length > 0 || sellingAppointments.length > 0) {
      // First check if we have appointments in the current tab
      const appointments = activeTab === "buying" ? buyingAppointments : sellingAppointments;
      if (appointments && appointments.length > 0) {
        const sampleAppointment = appointments[0];
        console.log("Sample appointment:", sampleAppointment);
        if (sampleAppointment && sampleAppointment.timeslot) {
          console.log("Timeslot data:", sampleAppointment.timeslot);
        }
      }
    }
  }, [buyingAppointments, sellingAppointments, activeTab]);

  // Function to safely display appointment date/time
  const renderAppointmentDateTime = (appointment) => {
    if (!appointment) return <span className="text-muted">No appointment data</span>;
    
    // Handle case where timeslot is a direct property with startDateTime and endDateTime
    if (appointment.timeslot?.startDateTime) {
      return (
        <>
          <p className="mb-1">
            <strong>Start Date & Time: </strong>
            {formatDateTime(appointment.timeslot.startDateTime)}
          </p>
          {appointment.timeslot.endDateTime && (
            <p className="mb-1">
              <strong>End Date & Time: </strong>
              {formatDateTime(appointment.timeslot.endDateTime)}
            </p>
          )}
        </>
      );
    }
    
    // Handle case where we need to access the embedded timeslot directly
    if (appointment.startDateTime) {
      return (
        <>
          <p className="mb-1">
            <strong>Start Date & Time: </strong>
            {formatDateTime(appointment.startDateTime)}
          </p>
          {appointment.endDateTime && (
            <p className="mb-1">
              <strong>End Date & Time: </strong>
              {formatDateTime(appointment.endDateTime)}
            </p>
          )}
        </>
      );
    }
    
    return <span className="text-muted">Pending scheduling</span>;
  };

  const cancelAppointment = async (id) => {
    // Customize confirmation message based on the active tab
    const confirmMessage = activeTab === "buying" 
      ? "Are you sure you want to cancel your booking for this appointment?" 
      : "Are you sure you want to cancel this appointment? This will remove the current buyer.";
    
    if (!window.confirm(confirmMessage)) {
      return;
    }
    
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${API_URL}/appointments/${id}/cancel`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.status} - ${await response.text()}`);
      }

      const updatedTransaction = await response.json();
      console.log("Cancellation response:", updatedTransaction);
      
      toast.success("Appointment cancelled successfully");
      
      // If we're in the buying tab, remove this appointment from buyingAppointments
      if (activeTab === "buying") {
        setBuyingAppointments(prev => prev.filter(apt => apt.id !== id));
      } else {
        // If we're in the selling tab, update the appointment to show no buyer
        setSellingAppointments(prev => prev.map(apt => 
          apt.id === id ? { ...apt, buyer: null, buyerId: null } : apt
        ));
      }
      
      // Refresh appointments from server to ensure data consistency
      fetchAppointments();
    } catch (error) {
      console.error("Error cancelling appointment:", error);
      toast.error("Failed to cancel appointment. Please try again.");
    }
  };

  // Function to fetch appointments from the server
  const fetchAppointments = async () => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        setError("You must be logged in to view your appointments");
        return;
      }

      const response = await fetch(`${API_URL}/user/appointments`, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json"
        }
      });

      if (!response.ok) {
        throw new Error(`Error: ${response.status} - ${await response.text()}`);
      }

      const data = await response.json();
      console.log("Refreshed appointments:", data);
      
      // Process buying appointments - ensure they have valid buyer and seller usernames
      const validBuyingAppointments = (data.buying || []).filter(apt => 
        apt.seller?.username && apt.buyer?.username
      );
      
      // Process selling appointments - ensure they have valid buyer username
      // and don't overlap with buying appointments (using transaction ID)
      const buyingIds = new Set(validBuyingAppointments.map(apt => apt.id));
      const validSellingAppointments = (data.selling || []).filter(apt => 
        apt.buyer?.username && !buyingIds.has(apt.id)
      );
      
      setBuyingAppointments(validBuyingAppointments);
      setSellingAppointments(validSellingAppointments);
    } catch (error) {
      console.error("Error refreshing appointments:", error);
      toast.error("Failed to refresh appointments");
    }
  };

  // No need for additional filtering since we're doing it at data load time
  const currentAppointments = activeTab === "buying" ? buyingAppointments : sellingAppointments;
  
  // Check if there are any appointments at all
  const hasNoAppointments = buyingAppointments.length === 0 && sellingAppointments.length === 0;
    
  const noAppointmentsMessage = activeTab === "buying"
    ? "You don't have any booked appointments yet."
    : "You don't have any appointments for your listings yet.";

  // Make sure we fetch appointments on component mount
  useEffect(() => {
    fetchAppointments();
  }, []);

  return (
    <>
      <Navbar />
      <div className="container py-5">
        <div className="row mb-4">
          <div className="col-12">
            <h2 className="display-6 fw-bold">My Appointments</h2>
            <hr />
          </div>
        </div>

        {/* Tabs */}
        <ul className="nav nav-tabs mb-4">
          <li className="nav-item">
            <button 
              className={`nav-link ${activeTab === "buying" ? "active" : ""}`} 
              onClick={() => setActiveTab("buying")}
            >
              Appointments I've Booked
            </button>
          </li>
          <li className="nav-item">
            <button 
              className={`nav-link ${activeTab === "selling" ? "active" : ""}`} 
              onClick={() => setActiveTab("selling")}
            >
              Appointments for My Listings
            </button>
          </li>
        </ul>

        {loading ? (
          <div className="text-center py-5">
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
            <p className="mt-3">Loading your appointments...</p>
          </div>
        ) : error ? (
          <div className="alert alert-danger">{error}</div>
        ) : hasNoAppointments ? (
          <div className="alert alert-info">
            <p className="mb-0">
              You don't have any appointments yet.
              <br />
              <a href="/products" className="alert-link">Browse products</a> to schedule appointments.
            </p>
          </div>
        ) : currentAppointments.length === 0 ? (
          <div className="alert alert-info">
            <p className="mb-0">{noAppointmentsMessage}</p>
          </div>
        ) : (
          <div className="row">
            {currentAppointments.map((appointment) => (
              <div className="col-md-6 mb-4" key={appointment.id || appointment._id}>
                <div className="card h-100 shadow-sm">
                  <div className="card-header d-flex justify-content-between align-items-center">
                    <h5 className="mb-0">{appointment.transactionName || appointment.item?.itemName || "Appointment"}</h5>
                    <span className="badge bg-primary">{appointment.status || "Scheduled"}</span>
                  </div>
                  <div className="card-body">
                    <div className="row mb-3">
                      <div className="col-md-12">
                        {renderAppointmentDateTime(appointment)}
                        <p className="mb-1"><strong>Location: </strong>{appointment.transactionLocation || "Not specified"}</p>
                      </div>
                      <div className="col-md-12 mt-2">
                        {activeTab === "buying" ? (
                          <>
                            <p className="mb-1"><strong>Seller: </strong>{appointment.seller?.username || "Unknown"}</p>
                            <p className="mb-1">
                              <strong>Contact: </strong>{appointment.seller?.email || "Not available"}
                            </p>
                          </>
                        ) : (
                          <>
                            <p className="mb-1"><strong>Buyer: </strong>{appointment.buyer?.username || "Unknown"}</p>
                            <p className="mb-1">
                              <strong>Contact: </strong>{appointment.buyer?.email || "Not available"}
                            </p>
                          </>
                        )}
                      </div>
                    </div>
                    
                    <p className="mb-0 text-muted">
                      <small>
                        <strong>Item:</strong> {appointment.item?.itemName || "Not available"}
                        {appointment.item?.itemPrice && (
                          <> - ${appointment.item.itemPrice}</>
                        )}
                      </small>
                    </p>
                  </div>
                  <div className="card-footer bg-white">
                    <button 
                      className="btn btn-sm btn-outline-danger float-end"
                      onClick={() => cancelAppointment(appointment.id || appointment._id)}
                    >
                      {activeTab === "buying" ? "Cancel My Booking" : "Cancel Appointment"}
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
      <Footer />
      <ToastContainer position="bottom-right" autoClose={3000} />
    </>
  );
};

export default MyAppointments; 