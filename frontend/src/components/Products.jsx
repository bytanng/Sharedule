import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import Skeleton from "react-loading-skeleton";
import { getProducts, searchProducts } from "../utils/ItemRoutes";

const Products = () => {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(null);
  const navigate = useNavigate();
  const [query, setQuery] = useState("");

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = async () => {
    const data = await getProducts();
    if (data) {
      setProducts(data);
    }
    setLoading(false);
  };

  const handleSearch = async () => {
    if (query.trim() === "") {
      fetchProducts(); // Reset to all items if query is empty
    } else {
      setLoading(true);
      const data = await searchProducts(query);
      setProducts(data);
      setLoading(false);
    }
  };

  const Loading = () => {
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
  };

  const DisplayProducts = () => {
    return (
      <>
        <div className="row justify-content-center">
          <div className="row">
            {products.length === 0 ? (
              <p>No items found.</p>
            ) : (
              products.map((product) => (
                <div
                  id={product.id}
                  key={product.id}
                  className="col-md-4 col-sm-6 col-xs-8 col-12 mb-4"
                  onClick={() => navigate(`/product/${product.id}`)} // Navigate on card click
                  style={{ cursor: "pointer" }} // Change cursor to pointer on hover
                >
                  <div className="card text-center h-100" key={product.id}>
                    <img
                      className="card-img-top p-3"
                      src={product.itemImage}
                      alt="Card"
                      height={300}
                      style={{ objectFit: "contain" }}
                    />
                    <div className="card-body">
                      <h5 className="card-title">
                        {product.itemName.substring(0, 12)}...
                      </h5>
                      <p className="card-text">
                        {product.itemDescription.substring(0, 90)}...
                      </p>
                    </div>
                    <ul className="list-group list-group-flush">
                      <li className="list-group-item lead">
                        $ {product.itemPrice}
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
        <div>
          <h2 className="text-center mb-4">Products and Services</h2>
          <div className="d-flex justify-content-center align-items-center mb-4">
            <input
              className="form-control me-2 w-50"
              type="text"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyPress={(e) => e.key === "Enter" && handleSearch()}
              placeholder="Search items..."
            />
            <button className="btn btn-secondary" onClick={handleSearch}>
              Search
            </button>
          </div>
        </div>
        <hr />
        {loading ? <Loading /> : <DisplayProducts />}
      </div>
      <Footer />
    </>
  );
};

export default Products;
