import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Footer, Navbar } from "../components";
import Skeleton from "react-loading-skeleton";
import { getItem, deleteItem } from "../utils/ItemRoutes";
import DeleteModal from "../components/DeleteModal";
import { getUserTransactions } from "../utils/TransactionRoutes";
import { getTimeslotByTransaction } from "../utils/TimeslotRoutes";
import dayjs from "dayjs";

const Item = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [item, setItem] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isModalOpen, setModalOpen] = useState(false);
  const [expandedTransaction, setExpandedTransaction] = useState(null); // Track the expanded transaction
  const [timeslotDetails, setTimeslotDetails] = useState({});

  useEffect(() => {
    const showItem = async () => {
      setLoading(true);

      try {
        const data = await getItem(id);
        const transactions = await getUserTransactions();

        setItem(data);
        setTransactions(transactions);
      } catch (error) {
        setItem(null);
        setTransactions([]);
      }

      setLoading(false);
    };

    showItem();
  }, [id]);

  const handleDeleteClick = () => {
    setModalOpen(true);
  };

  const handleConfirmDelete = (e) => {
    e.preventDefault();
    deleteItem(localStorage.getItem("token"), item.id);
    setModalOpen(false);
    alert("Item deleted");
    navigate("/view-listings");
  };

  if (!item) {
    return <div>You are not authorized to view this item</div>; // Handle the case when the item is not found
  }

  const routeToEditItem = () => {
    navigate(`/item/edit/${id}`);
  };

  const routeToCreateTransaction = () => {
    navigate(`/create-transaction/${id}`);
  };

  const handleDateFormat = (isoString) => {
    return dayjs(isoString).format("DD/MM/YYYY, HH:mm");
  };

  const toggleTransactionDetails = (transactionName) => {
    setExpandedTransaction(
      expandedTransaction === transactionName ? null : transactionName
    );
  };

  const handleTimeslotDetails = async (transactionId) => {
    const timeslot = await getTimeslotByTransaction(transactionId);
    setTimeslotDetails(timeslot);
  };

  const Loading =
    (() => {
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
    },
    []);

  const DisplayItem = ({ item }) => {
    return (
      <>
        <div className="container my-5 py-2">
          <div className="row">
            <div className="col-md-6 col-sm-12 py-3">
              <img
                className="img-fluid"
                src={item.itemImage}
                alt={item.itemName}
                width="400px"
                height="400px"
              />
            </div>
            <div className="col-md-6 col-md-6 py-5">
              <h1 className="display-5">{item.itemName}</h1>
              <h3 className="display-6  my-4">${item.itemPrice}</h3>
              <p className="lead">{item.itemDescription}</p>
              <button
                className="btn btn-primary m-1"
                onClick={() => routeToEditItem()}
              >
                Edit Item
              </button>
              <button
                className="btn btn-primary btn-danger"
                onClick={handleDeleteClick}
              >
                Delete Item
              </button>
            </div>
          </div>

          <div className="mt-5">
            <div className="d-flex justify-content-between align-items-center mt-5">
              <h2>Availabilities</h2>
              <button
                className="btn btn-primary"
                onClick={routeToCreateTransaction}
              >
                Create Transaction
              </button>
            </div>
            {transactions.length > 0 ? (
              <ul className="list-group mt-3">
                {transactions.map((transaction) => {
                  const isReserved =
                    transaction.buyerId !== null && transaction.buyerId !== "";

                  return (
                    <li
                      key={transaction.transactionName}
                      className="list-group-item justify-content-between align-items-center mb-2 p-3 fs-5 
                      border border-2 border-dark"
                      style={{
                        cursor: "pointer",
                        borderRadius: "8px",
                        borderTop: "2px solid black",
                      }}
                      onClick={() => {
                        toggleTransactionDetails(transaction.transactionName);
                        handleTimeslotDetails(transaction.id);
                      }}
                    >
                      <div className="d-flex justify-content-between w-100">
                        <div>
                          <strong>{transaction.transactionName}</strong>
                        </div>
                        <span
                          className={`badge fs-6 px-3 py-2 ${
                            isReserved ? "bg-secondary" : "bg-success"
                          }`}
                        >
                          {isReserved ? "Reserved" : "Available"}
                        </span>
                      </div>

                      {expandedTransaction === transaction.transactionName && (
                        <div className="mt-3 ps-4">
                          <p>
                            <strong>Buyer ID: </strong> {transaction.buyerId}
                          </p>
                          <p>
                            <strong>Start Date & Time: </strong>
                            {handleDateFormat(timeslotDetails.startDateTime)}
                          </p>
                          <p>
                            <strong>End Date & Time: </strong>
                            {handleDateFormat(timeslotDetails.endDateTime)}
                          </p>
                          <p>
                            <strong>Location: </strong>
                            {transaction.transactionLocation}
                          </p>
                        </div>
                      )}
                    </li>
                  );
                })}
              </ul>
            ) : (
              <p className="mt-3">No transactions available.</p>
            )}
          </div>
        </div>
      </>
    );
  };

  return (
    <>
      <Navbar />
      <div className="container">
        <div className="row">
          {loading ? <Loading /> : <DisplayItem item={item} />}
        </div>
      </div>
      <DeleteModal
        isOpen={isModalOpen}
        onClose={() => setModalOpen(false)}
        onConfirm={handleConfirmDelete}
      />
      <Footer />
    </>
  );
};

export default Item;
