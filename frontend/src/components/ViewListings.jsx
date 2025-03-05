import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from './Navbar';
import Footer from './Footer';
import Skeleton from "react-loading-skeleton";
import { getUserItems } from '../utils/UserRoutes';
import { Link } from "react-router-dom";
import toast from "react-hot-toast";

const ViewListings = () => {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(null);

    useEffect(() => {
        const fetchItems = async () => {
            const data = await getUserItems();
            if (data) {
                console.log(data);
                setItems(data);
            }
            setLoading(false);
        };

        fetchItems();

    }, []);

  const Loading = (() => {
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
  },[]);
  
    return (
        <>
            <Navbar />
            <div className="container my-5">
                <h2 className="text-center mb-4">My Listings</h2>
                <div className="row justify-content-center">
                    <div className="container my-5 py-2">
                        <div className="row">
                        {items.length === 0 ? (
                            <p>No items found.</p>
                        ) : (
                            <ul>
                                {items.map((item) => (
                                    <div
                                    id={item.id}
                                    key={item.id}
                                    className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4"
                                    >
                                        <div className="card text-center h-100" key={item.id}>
                                            <img
                                            className="card-img-top p-3"
                                            src={item.itemImage}
                                            alt="Card"
                                            height={300}
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
                                            <li className="list-group-item lead">$ {item.price}</li>
                                            {/* <li className="list-group-item">Dapibus ac facilisis in</li>
                                                <li className="list-group-item">Vestibulum at eros</li> */}
                                            </ul>
                                            <div className="card-body">

                                            <button
                                                className="btn btn-primary m-1"
                                                onClick={() => {
                                                }}
                                            >
                                                Edit Item
                                            </button>
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </ul>
                        )}
                        </div>
                    </div>
                </div>
            </div>
            <Footer />
        </>
    );
};

export default ViewListings; 