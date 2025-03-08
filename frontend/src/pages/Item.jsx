import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { Footer, Navbar } from "../components";
import Skeleton from "react-loading-skeleton";
import { getItem } from "../utils/ItemRoutes";
import { useNavigate } from 'react-router-dom';

const Item = () => {

  const navigate = useNavigate();
  const { id } = useParams();
  const [item, setItem] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const showItem = async () => {
      setLoading(true);

      try {
        const data = await getItem(id);

        setItem(data);
      } catch (error) {
        setItem(null);
      }

      setLoading(false);
    };

    showItem();
  }, [id]);

  if (!item) {
    return <div>You are not authorized to view this item</div>; // Handle the case when the item is not found
  }

  const routeToEditItem = () => {
    navigate(`/item/edit/${id}`)
  }

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
              <button className="btn btn-primary m-1" onClick={() => routeToEditItem()}>
                Edit Item
              </button>
              <button className="btn btn-primary btn-danger" onClick={() => {}}>
                Delete Item
              </button>
            </div>
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
      <Footer />
    </>
  );
};

export default Item;
