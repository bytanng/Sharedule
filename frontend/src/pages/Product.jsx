import React, { useEffect, useState, useCallback, useMemo } from "react";
import { useParams } from "react-router-dom";
import { Footer, Navbar } from "../components";
import Skeleton from "react-loading-skeleton";
import { getProduct } from "../utils/ItemRoutes";
import { API_URL } from "../utils/Constants";
import { getUserTransactions, bookTransaction } from "../utils/TransactionRoutes";
import { getTimeslotByTransaction } from "../utils/TimeslotRoutes";
import dayjs from "dayjs";
import { ToastContainer, toast } from "react-toastify";
import "react-toastify/dist/ReactToastify.css";
import { Modal, Button } from "react-bootstrap";
import "bootstrap-icons/font/bootstrap-icons.css";

const Product = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(false);
  const [transactions, setTransactions] = useState([]);
  const [showTransactions, setShowTransactions] = useState(false);
  const [loadingTransactions, setLoadingTransactions] = useState(false);
  const [timeslotDetails, setTimeslotDetails] = useState({});
  const [bookingInProgress, setBookingInProgress] = useState(false);
  const [successModalVisible, setSuccessModalVisible] = useState(false);
  const [bookedTransaction, setBookedTransaction] = useState(null);
  const [sellerContact, setSellerContact] = useState("");
  const [currentBookingId, setCurrentBookingId] = useState(null);

  // Format date and time
  const formatDateTime = useCallback((isoString) => {
    if (!isoString) return "Not specified";
    return dayjs(isoString).format("DD/MM/YYYY, HH:mm");
  }, []);

  // Load product data
  useEffect(() => {
    const showProduct = async () => {
      setLoading(true);

      try {
        const data = await getProduct(id);
        setProduct(data);
      } catch (error) {
        setProduct(null);
      }

      setLoading(false);
    };

    showProduct();
  }, [id]);

  // Fetch available timeslots
  const fetchAvailableTimeslots = useCallback(async () => {
    setLoadingTransactions(true);
    setShowTransactions(true);
    
    try {
      const data = await getUserTransactions(id);
      setTransactions(data || []);
      
      // Fetch timeslot details for each transaction
      const timeslotMap = {};
      for (const transaction of data || []) {
        if (transaction.id) {
          try {
            const timeslot = await getTimeslotByTransaction(transaction.id);
            timeslotMap[transaction.id] = timeslot;
          } catch (error) {
            console.error(`Error fetching timeslot for transaction ${transaction.id}:`, error);
          }
        }
      }
      setTimeslotDetails(timeslotMap);
    } catch (error) {
      console.error("Error fetching available timeslots:", error);
      setTransactions([]);
    }
    
    setLoadingTransactions(false);
  }, [id]);

  // Function to handle booking a transaction
  const handleBooking = useCallback(async (transactionId) => {
    setCurrentBookingId(transactionId);
    setBookingInProgress(true);
    
    try {
      const result = await bookTransaction(transactionId);
      
      if (result.success) {
        const transaction = result.data;
        // Get seller email, fallback to ID if unavailable
        let contactInfo = "the seller through the platform";
        
        if (transaction.seller && transaction.seller.email) {
          contactInfo = transaction.seller.email;
        } else if (transaction.sellerName) {
          contactInfo = transaction.sellerName;
        }
        
        // Save booked transaction info for modal
        setBookedTransaction(transaction);
        setSellerContact(contactInfo);
        setSuccessModalVisible(true);
        
        toast.success(
          <div>
            <p><strong>Your appointment has been Shareduled!</strong></p>
            <p>Please contact the seller at {contactInfo}</p>
          </div>,
          { 
            autoClose: 8000,
            style: { fontSize: '1rem' }
          }
        );
        // Refresh the list of available timeslots
        await fetchAvailableTimeslots();
      } else {
        toast.error(result.error || 'Failed to book transaction. Please try again.');
      }
    } catch (error) {
      console.error("Error scheduling transaction:", error);
      toast.error('An unexpected error occurred. Please try again later.');
    } finally {
      setBookingInProgress(false);
      setCurrentBookingId(null);
    }
  }, [fetchAvailableTimeslots]);

  // Loading component
  const Loading = useMemo(() => {
    return (
      <>
        <div className="col-12 py-5 text-center">
          <Skeleton height={40} width={560} />
        </div>
        <div className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4">
          <Skeleton height={592} />
        </div>
        <div className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4">
          <Skeleton height={592} />
        </div>
        <div className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4">
          <Skeleton height={592} />
        </div>
        <div className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4">
          <Skeleton height={592} />
        </div>
        <div className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4">
          <Skeleton height={592} />
        </div>
        <div className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4">
          <Skeleton height={592} />
        </div>
      </>
    );
  }, []);

  // TimeslotsList component
  const TimeslotsList = useMemo(() => {
    if (loadingTransactions) {
      return (
        <div className="mt-3 text-center p-4">
          <div className="spinner-border text-primary me-2" role="status">
            <span className="visually-hidden">Loading...</span>
          </div>
          <span className="h5 ms-2">Loading available timeslots...</span>
        </div>
      );
    }

    if (transactions.length === 0) {
      return (
        <div className="mt-3 alert alert-info p-4 text-center">
          <i className="bi bi-info-circle me-2"></i>
          No available timeslots found for this item.
        </div>
      );
    }

    return (
      <div className="mt-3">
        <div className="card">
          <div className="card-header bg-dark text-white">
            <h4 className="mb-0">Available Timeslots</h4>
          </div>
          <div className="card-body p-0">
            <div className="list-group list-group-flush">
              {transactions.map((transaction, index) => {
                const isReserved = transaction.buyerId && transaction.buyerId.trim() !== '';
                const isEvenRow = index % 2 === 0;
                const timeslot = timeslotDetails[transaction.id] || {};
                const isCurrentlyBooking = currentBookingId === transaction.id && bookingInProgress;
                
                return (
                  <div 
                    key={transaction.id} 
                    className={`list-group-item list-group-item-action p-4 border-bottom ${isEvenRow ? '' : 'bg-light'}`}
                  >
                    <div className="row align-items-center">
                      <div className="col-md-8">
                        <h5 className="mb-1 fw-bold">
                          {transaction.transactionName}
                        </h5>
                        <p className="mb-1 text-muted">
                          <strong>Location and Others: </strong>{transaction.transactionLocation || 'No location specified'}
                        </p>
                        {timeslot && (
                          <div>
                            <p className="mb-1 text-muted">
                              <strong>Start: </strong>{formatDateTime(timeslot.startDateTime)}
                            </p>
                            <p className="mb-1 text-muted">
                              <strong>End: </strong>{formatDateTime(timeslot.endDateTime)}
                            </p>
                          </div>
                        )}
                      </div>
                      <div className="col-md-4 text-md-end mt-3 mt-md-0">
                        <span className={`badge ${isReserved ? 'bg-secondary' : 'bg-success'} p-2 mb-2 d-block d-md-inline-block`}>
                          {isReserved ? 'Reserved' : 'Available'}
                        </span>
                        <button 
                          className="btn btn-primary ms-md-2 mt-2 mt-md-0" 
                          onClick={() => handleBooking(transaction.id)}
                          disabled={isReserved || bookingInProgress}
                        >
                          {isCurrentlyBooking ? (
                            <>
                              <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                              Booking...
                            </>
                          ) : isReserved ? 'Already Booked' : 'Book This Timeslot'}
                        </button>
                      </div>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>
        </div>
      </div>
    );
  }, [
    loadingTransactions, 
    transactions, 
    timeslotDetails, 
    bookingInProgress, 
    formatDateTime, 
    handleBooking, 
    currentBookingId
  ]);

  // Success Modal Component
  const SuccessModal = useMemo(() => {
    if (!successModalVisible) return null;
    
    const transaction = bookedTransaction || {};
    const timeslot = transaction.id ? timeslotDetails[transaction.id] || {} : {};
    
    return (
      <Modal show={successModalVisible} onHide={() => setSuccessModalVisible(false)} centered>
        <Modal.Header closeButton className="bg-success text-white">
          <Modal.Title>Booking Successful!</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="text-center mb-4">
            <i className="bi bi-check-circle-fill text-success" style={{ fontSize: '3rem' }}></i>
          </div>
          <h4 className="text-center mb-4">{transaction.transactionName}</h4>
          
          <div className="card mb-3">
            <div className="card-body">
              <h5 className="card-title">Appointment Details</h5>
              <p><strong>Location and Others:</strong> {transaction.transactionLocation}</p>
              {timeslot && (
                <>
                  <p><strong>Start:</strong> {formatDateTime(timeslot.startDateTime)}</p>
                  <p><strong>End:</strong> {formatDateTime(timeslot.endDateTime)}</p>
                </>
              )}
            </div>
          </div>
          
          <div className="alert alert-info">
            <p className="mb-1"><strong>Important:</strong></p>
            <p>Please contact the seller at {sellerContact}</p>
            <p className="small mb-0">Save this contact information for future reference.</p>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setSuccessModalVisible(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    );
  }, [successModalVisible, bookedTransaction, timeslotDetails, sellerContact, formatDateTime]);

  // Render DisplayProduct component based on product
  const DisplayProductContent = useMemo(() => {
    if (!product) return null;
    
    return (
      <>
        <div className="container my-5 py-2">
          <div className="row">
            <div className="col-md-6 col-sm-12 py-3">
              <img
                className="img-fluid"
                src={product.itemImage}
                alt={product.itemName}
                width="400px"
                height="400px"
              />
            </div>
            <div className="col-md-6 col-md-6 py-5">
              <h1 className="display-5">{product.itemName}</h1>
              <h3 className="display-6 my-4">${product.itemPrice}</h3>
              <p className="lead">{product.itemDescription}</p>
              <button 
                className="btn btn-dark m-1" 
                onClick={fetchAvailableTimeslots}
              >
                Schedule Transaction
              </button>
            </div>
          </div>
          
          {/* Timeslots section - full width */}
          {showTransactions && (
            <div className="row mt-4">
              <div className="col-12">
                {TimeslotsList}
              </div>
            </div>
          )}
        </div>
      </>
    );
  }, [product, showTransactions, fetchAvailableTimeslots, TimeslotsList]);

  if (!product) {
    return <div>You are not authorized to view this item</div>; // Handle the case when the item is not found
  }

  // Main render
  return (
    <>
      <Navbar />
      <div className="container">
        <div className="row">
          {loading ? Loading : DisplayProductContent}
        </div>
      </div>
      <Footer />
      <ToastContainer position="bottom-right" autoClose={5000} hideProgressBar={false} />
      {SuccessModal}
    </>
  );
};

export default Product;
