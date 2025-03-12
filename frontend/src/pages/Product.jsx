import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Footer, Navbar } from "../components";
import Skeleton from "react-loading-skeleton";
import { getProduct } from "../utils/ItemRoutes";

const Product = () => {
  const { id } = useParams();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

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

  if (!product) {
    return <div>You are not authorized to view this item</div>; // Handle the case when the item is not found
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

  const DisplayProduct = ({ product }) => {
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
              <h3 className="display-6  my-4">${product.itemPrice}</h3>
              <p className="lead">{product.itemDescription}</p>
              <button className="btn btn-dark m-1" onClick={() => {}}>
                Schedule Transaction
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
          {loading ? <Loading /> : <DisplayProduct product={product} />}
        </div>
      </div>
      <Footer />
    </>
  );
};

export default Product;
