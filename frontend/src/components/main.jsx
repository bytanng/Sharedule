import React from "react";

const Home = () => {
  return (
    <>
      <div className="hero border-1 pb-3">
        <div className="card bg-dark text-white border-0 mx-3">
          <img
            className="card-img img-fluid"
            src="./assets/main.png.jpg"
            alt="Card"
            height={500}
          />
          <div className="card-img-overlay d-flex align-items-center">
            <div className="container">
              <h5 className="card-title fs-1 text fw-lighter">Sharedule</h5>
              <p className="card-text fs-5 d-none d-sm-block ">
                Sharedule is a dynamic marketplace platform designed to
                streamline transactions by integrating seamless scheduling
                capabilities. Unlike many existing platforms, it enables sellers
                to showcase their services or products while efficiently
                managing availability and accepting appointments in real-time.
                Buyers can explore offerings, book appointments effortlessly,
                and complete transactions with ease. Key features such as
                automated reminders, real-time notifications, and a feedback
                system enhance user experience and foster trust. By prioritizing
                convenience, clarity, and efficiency, Sharedule aims to
                revolutionize how buyers and sellers connect in today’s
                service-driven economy.
              </p>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Home;
