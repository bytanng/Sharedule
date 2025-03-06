import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import Skeleton from "react-loading-skeleton";
import { getUserItems } from "../utils/UserRoutes";

const ViewListings = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchItems = async () => {
      const data = await getUserItems();
      if (data) {
        setItems(data);
      }
      setLoading(false);
    };

    fetchItems();
  }, []);

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

  const DisplayMyListings = () => {
    return (
      <>
        <h2 className="text-center mb-4">My Listings</h2>
        <div className="row justify-content-center">
          <div className="row">
            {items.length === 0 ? (
              <p>No items found.</p>
            ) : (
              items.map((item) => (
                <div
                  id={item.id}
                  key={item.id}
                  className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4"
                  onClick={() => navigate(`/item/${item.id}`)} // Navigate on card click
                  style={{ cursor: "pointer" }} // Change cursor to pointer on hover
                >
                  <div className="card text-center h-100" key={item.id}>
                    <img
                      className="card-img-top p-3"
                      src={item.itemImage}
                      alt="Card"
                      height={300}
                      style={{ objectFit: "contain" }}
                    />
                    <div className="card-body">
                      <h5 className="card-title">
                        {item.itemName.substring(0, 12)}...
                      </h5>
                      <p className="card-text">
                        {item.itemDescription.substring(0, 90)}...
                      </p>
                    </div>
                    <ul className="list-group list-group-flush">
                      <li className="list-group-item lead">
                        $ {item.itemPrice}
                      </li>
                    </ul>
                  </div>
                </div>
              ))
            )}
          </div>
        </div>
      </>
    );
  };

  return (
    <>
      <Navbar />
      <div className="container my-5">
        {loading ? <Loading /> : <DisplayMyListings />}
      </div>
      <Footer />
    </>
  );
};

export default ViewListings;
