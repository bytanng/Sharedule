import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Footer, Navbar } from "../components";
import { getProduct } from "../utils/ItemRoutes";
import { createTransaction } from "../utils/TransactionRoutes";
import { getAvailableTimeslotsByItemId } from "../utils/TimeslotRoutes";
import Skeleton from "react-loading-skeleton";
import "react-loading-skeleton/dist/skeleton.css";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';

const ScheduleTransaction = () => {
  const { id } = useParams(); // Get the product ID from URL
  const navigate = useNavigate();
  
  // State variables
  const [product, setProduct] = useState(null);
  const [availableTimeslots, setAvailableTimeslots] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [selectedTimeslot, setSelectedTimeslot] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [timeslotsByDate, setTimeslotsByDate] = useState({});
  const [error, setError] = useState(null);
  const [transactionData, setTransactionData] = useState({
    transactionName: "",
    transactionLocation: "",
    notes: "",
  });

  // Load the product details
  useEffect(() => {
    const fetchProduct = async () => {
      try {
        setError(null);
        const data = await getProduct(id);
        setProduct(data);
        
        // After loading product, fetch available timeslots
        fetchAvailableTimeslots(data);
      } catch (error) {
        console.error("Error fetching product:", error);
        setError("Failed to load product details. Please try again later.");
        toast.error("Failed to load product details");
      } finally {
        setLoading(false);
      }
    };
    
    fetchProduct();
  }, [id]);

  // Fetch available timeslots for the product
  const fetchAvailableTimeslots = async (product) => {
    if (!product || !product.id) {
      setAvailableTimeslots([]);
      return;
    }
    
    try {
      setError(null);
      const timeslots = await getAvailableTimeslotsByItemId(product.id);
      setAvailableTimeslots(timeslots);
      
      // Organize timeslots by date for the calendar view
      const slotsByDate = {};
      timeslots.forEach(slot => {
        const date = new Date(slot.startDateTime).toDateString();
        if (!slotsByDate[date]) {
          slotsByDate[date] = [];
        }
        slotsByDate[date].push(slot);
      });
      setTimeslotsByDate(slotsByDate);
      
      if (timeslots.length === 0) {
        toast.info("No available timeslots found for this item. Please check back later.");
      }
    } catch (error) {
      console.error("Error fetching timeslots:", error);
      setError("Failed to load available time slots. Please try again later.");
      toast.error("Failed to load available time slots");
      setAvailableTimeslots([]);
    }
  };

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setTransactionData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // Handle timeslot selection
  const handleTimeslotSelect = (timeslot) => {
    setSelectedTimeslot(timeslot);
  };

  // Handle date selection from calendar
  const handleDateChange = (date) => {
    setSelectedDate(date);
    // If user selects a date with no timeslots, show a message
    const dateString = date.toDateString();
    if (!timeslotsByDate[dateString] || timeslotsByDate[dateString].length === 0) {
      toast.info("No available timeslots for this date. Please select another date.");
    }
  };

  // Determine if a date has timeslots
  const tileClassName = ({ date }) => {
    const dateString = date.toDateString();
    return timeslotsByDate[dateString] && timeslotsByDate[dateString].length > 0 
      ? 'has-timeslots' 
      : null;
  };

  // Format date for display
  const formatDateTime = (dateTimeStr) => {
    if (!dateTimeStr) return "N/A";
    const date = new Date(dateTimeStr);
    return date.toLocaleString("en-US", {
      weekday: 'short',
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      hour12: true
    });
  };

  // Format time only for display
  const formatTimeOnly = (dateTimeStr) => {
    if (!dateTimeStr) return "N/A";
    const date = new Date(dateTimeStr);
    return date.toLocaleString("en-US", {
      hour: 'numeric',
      minute: 'numeric',
      hour12: true
    });
  };

  // Handle booking submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!selectedTimeslot) {
      toast.warning("Please select a timeslot");
      return;
    }
    
    if (!transactionData.transactionName || !transactionData.transactionLocation) {
      toast.warning("Please fill in all required fields");
      return;
    }
    
    try {
      setSubmitting(true);
      setError(null);
      
      const transaction = {
        ...transactionData,
        timeslotId: selectedTimeslot.id
      };
      
      const response = await createTransaction(id, transaction);
      
      if (response.success) {
        toast.success("Transaction scheduled successfully!");
        setTimeout(() => navigate("/user/profile"), 2000); // Redirect after 2 seconds
      } else {
        throw new Error(response.error);
      }
    } catch (error) {
      console.error("Error booking transaction:", error);
      setError("Failed to schedule transaction. Please try again later.");
      toast.error(error.message || "Failed to schedule transaction");
    } finally {
      setSubmitting(false);
    }
  };

  // Get filtered timeslots for the selected date
  const getFilteredTimeslots = () => {
    const dateString = selectedDate.toDateString();
    return timeslotsByDate[dateString] || [];
  };

  // Loading skeleton component
  const Loading = () => {
    return (
      <div className="container py-5">
        <div className="row">
          <div className="col-12 mb-4">
            <Skeleton height={40} width={400} />
          </div>
          <div className="col-md-6">
            <Skeleton height={300} />
          </div>
          <div className="col-md-6">
            <Skeleton height={50} width={300} className="mb-3" />
            <Skeleton height={40} count={3} className="mb-2" />
            <Skeleton height={50} width={200} className="mt-3" />
          </div>
        </div>
      </div>
    );
  };

  return (
    <>
      <Navbar />
      <div className="container py-5">
        {loading ? (
          <Loading />
        ) : error ? (
          <div className="alert alert-danger" role="alert">
            {error}
            <button 
              className="btn btn-outline-dark ms-3"
              onClick={() => navigate(-1)}
            >
              Go Back
            </button>
          </div>
        ) : (
          <>
            <div className="row mb-4">
              <div className="col-12">
                <h2 className="display-6 fw-bold">Schedule Transaction</h2>
                <hr />
              </div>
            </div>
            
            {product ? (
              <div className="row">
                {/* Product Details Section */}
                <div className="col-md-4 mb-4">
                  <div className="card shadow-sm">
                    <img 
                      src={product.itemImage || "https://via.placeholder.com/400x300?text=No+Image"}
                      className="card-img-top img-fluid" 
                      alt={product.itemName}
                      style={{ height: "250px", objectFit: "cover" }}
                    />
                    <div className="card-body">
                      <h5 className="card-title">{product.itemName}</h5>
                      <p className="card-text text-truncate">{product.itemDescription}</p>
                      <h6 className="fw-bold">${product.itemPrice}</h6>
                      <hr />
                      <p className="card-text">
                        <small className="text-muted">
                          Select a date and time slot to schedule your transaction with the seller.
                        </small>
                      </p>
                    </div>
                  </div>
                </div>
                
                {/* Booking Form Section */}
                <div className="col-md-8">
                  <div className="card shadow-sm">
                    <div className="card-body">
                      <h4 className="card-title mb-4">Book Your Transaction</h4>
                      
                      {availableTimeslots.length > 0 ? (
                        <form onSubmit={handleSubmit}>
                          <div className="row">
                            <div className="col-md-6 mb-3">
                              <label htmlFor="transactionName" className="form-label">Transaction Name*</label>
                              <input
                                type="text"
                                className="form-control"
                                id="transactionName"
                                name="transactionName"
                                value={transactionData.transactionName}
                                onChange={handleInputChange}
                                placeholder="E.g., Purchase of item"
                                required
                              />
                            </div>
                            
                            <div className="col-md-6 mb-3">
                              <label htmlFor="transactionLocation" className="form-label">Location*</label>
                              <input
                                type="text"
                                className="form-control"
                                id="transactionLocation"
                                name="transactionLocation"
                                value={transactionData.transactionLocation}
                                placeholder="E.g., Coffee shop at 123 Main St"
                                onChange={handleInputChange}
                                required
                              />
                            </div>
                          </div>
                          
                          <div className="mb-3">
                            <label htmlFor="notes" className="form-label">Additional Notes</label>
                            <textarea
                              className="form-control"
                              id="notes"
                              name="notes"
                              rows="2"
                              value={transactionData.notes}
                              onChange={handleInputChange}
                              placeholder="Any special requests or information for the seller"
                            ></textarea>
                          </div>
                          
                          <div className="row mb-4">
                            <div className="col-md-6 mb-3">
                              <label className="form-label d-block">Select Date</label>
                              <div className="calendar-container">
                                <Calendar 
                                  onChange={handleDateChange} 
                                  value={selectedDate} 
                                  tileClassName={tileClassName}
                                  minDate={new Date()}
                                />
                                <style>
                                  {`
                                    .has-timeslots {
                                      background-color: #e9f5e9;
                                      color: #000;
                                      font-weight: bold;
                                    }
                                    .react-calendar__tile--active {
                                      background: #007bff !important;
                                      color: white !important;
                                    }
                                  `}
                                </style>
                              </div>
                            </div>
                            
                            <div className="col-md-6">
                              <label className="form-label">Available Time Slots</label>
                              <div className="timeslots-container" style={{maxHeight: "300px", overflowY: "auto"}}>
                                {getFilteredTimeslots().length > 0 ? (
                                  getFilteredTimeslots().map((slot) => (
                                    <div 
                                      key={slot.id}
                                      className={`card p-3 mb-2 ${selectedTimeslot?.id === slot.id ? 'bg-light border-primary' : ''}`}
                                      onClick={() => handleTimeslotSelect(slot)}
                                      style={{ cursor: 'pointer' }}
                                    >
                                      <div className="d-flex justify-content-between align-items-center">
                                        <div>
                                          <div className="fw-bold">
                                            {formatTimeOnly(slot.startDateTime)}
                                          </div>
                                          <div className="small text-muted">
                                            to {formatTimeOnly(slot.endDateTime)}
                                          </div>
                                        </div>
                                        {selectedTimeslot?.id === slot.id && (
                                          <div className="badge bg-primary">Selected</div>
                                        )}
                                      </div>
                                    </div>
                                  ))
                                ) : (
                                  <div className="alert alert-info">
                                    No available time slots for this date. Please select another date.
                                  </div>
                                )}
                              </div>
                            </div>
                          </div>
                          
                          {selectedTimeslot && (
                            <div className="alert alert-info mb-3">
                              <strong>Selected Time:</strong> {formatDateTime(selectedTimeslot.startDateTime)} to {formatTimeOnly(selectedTimeslot.endDateTime)}
                            </div>
                          )}
                          
                          <button 
                            type="submit" 
                            className="btn btn-dark"
                            disabled={submitting || !selectedTimeslot}
                          >
                            {submitting ? 'Scheduling...' : 'Schedule Transaction'}
                          </button>
                          <button 
                            type="button" 
                            className="btn btn-outline-secondary ms-2"
                            onClick={() => navigate(-1)}
                          >
                            Cancel
                          </button>
                        </form>
                      ) : (
                        <div className="alert alert-warning">
                          <h5>No Available Timeslots</h5>
                          <p>The seller hasn't provided any available time slots for this item yet.</p>
                          <button 
                            className="btn btn-outline-dark mt-2"
                            onClick={() => navigate(-1)}
                          >
                            Go Back
                          </button>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            ) : (
              <div className="alert alert-danger">
                <h5>Item Not Found</h5>
                <p>The requested item could not be found or you are not authorized to view it.</p>
                <button 
                  className="btn btn-outline-dark mt-2"
                  onClick={() => navigate("/")}
                >
                  Return to Home
                </button>
              </div>
            )}
          </>
        )}
      </div>
      <ToastContainer position="bottom-right" />
      <Footer />
    </>
  );
};

export default ScheduleTransaction; 