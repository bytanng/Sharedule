import React, { useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import Navbar from "../components/Navbar";
import Footer from "../components/Footer";
import { toast } from "react-hot-toast";
import { createTransaction } from "../utils/TransactionRoutes";
import { createTimeslot } from "../utils/TimeslotRoutes";

const CreateTransaction = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [formData, setFormData] = useState({
    transactionName: "",
    buyerId: "",
    transactionLocation: "",
  });

  const [errors, setErrors] = useState({});

  const validateForm = () => {
    const newErrors = {};

    // Validate Name (required)
    if (!formData.transactionName) {
      newErrors.transactionName = "Name is required";
    }

    // Validate Location (required)
    if (!formData.transactionLocation) {
      newErrors.transactionLocation = "Location is required";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));

    // Clear error when user starts typing
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validate form before submission
    if (!validateForm()) {
      toast.error("Please fix the errors in the form");
      return; // Stop execution if form validation fails
    }

    // Check that end time is after start time before calling API
    const startDate = new Date(formData.startDateTime);
    const endDate = new Date(formData.endDateTime);

    if (isNaN(startDate) || isNaN(endDate)) {
      toast.error("Invalid start or end date.");
      return;
    }

    if (endDate <= startDate) {
      toast.error("End date and time must be after start date and time.");
      return;
    }

    // Call the API to create transaction
    const transactionData = await createTransaction(id, formData);

    if (!transactionData.success) {
      toast.error(transactionData.error);
      return; // Stop execution if transaction creation fails
    }

    toast.success("Availability created successfully!");
    navigate(`/item/${id}`); // Redirect to items page

    // Create timeslot after successful transaction
    const timeslotData = {
      startDateTime: startDate.toISOString(),
      endDateTime: endDate.toISOString(),
    };

    const timeslotCreated = await createTimeslot(
      timeslotData,
      transactionData.data.id
    );

    if (!timeslotCreated.success) {
      toast.error(timeslotCreated.error);
      return; // Stop execution if timeslot creation fails
    }

    // toast.success("Timeslot created successfully!");
  };

  return (
    <>
      <Navbar />
      <div className="container my-5">
        <h2 className="text-center mb-4">Create New Availability</h2>
        <div className="row justify-content-center">
          <div className="col-md-8">
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="transactionName" className="form-label">
                  Name
                </label>
                <input
                  type="text"
                  className={`form-control ${
                    errors.transactionName ? "is-invalid" : ""
                  }`}
                  id="transactionName"
                  name="transactionName"
                  value={formData.transactionName}
                  onChange={handleChange}
                  required
                />
                {errors.transactionName && (
                  <div className="invalid-feedback">
                    {errors.transactionName}
                  </div>
                )}
              </div>
              <div className="mb-3">
                <label htmlFor="startDateTime" className="form-label">
                  Start Date and Time
                </label>
                <input
                  type="datetime-local"
                  className={`form-control ${
                    errors.startDateTime ? "is-invalid" : ""
                  }`}
                  id="startDateTime"
                  name="startDateTime"
                  value={formData.startDateTime}
                  onChange={handleChange}
                  required
                />
                {errors.startDateTime && (
                  <div className="invalid-feedback">{errors.startDateTime}</div>
                )}
              </div>
              <div className="mb-3">
                <label htmlFor="endDateTime" className="form-label">
                  End Date and Time
                </label>
                <input
                  type="datetime-local"
                  className={`form-control ${
                    errors.endDateTime ? "is-invalid" : ""
                  }`}
                  id="endDateTime"
                  name="endDateTime"
                  value={formData.endDateTime}
                  onChange={handleChange}
                  required
                />
                {errors.endDateTime && (
                  <div className="invalid-feedback">{errors.endDateTime}</div>
                )}
              </div>
              <div className="mb-3">
                <label htmlFor="transactionLocation" className="form-label">
                  Location and Others
                </label>
                <input
                  type="text"
                  className={`form-control ${
                    errors.transactionLocation ? "is-invalid" : ""
                  }`}
                  id="transactionLocation"
                  name="transactionLocation"
                  value={formData.transactionLocation}
                  onChange={handleChange}
                  required
                />
                {errors.transactionLocation && (
                  <div className="invalid-feedback">
                    {errors.transactionLocation}
                  </div>
                )}
              </div>

              <div className="text-center">
                <button type="submit" className="btn btn-primary px-5">
                  Create Transaction
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
      <Footer />
    </>
  );
};

export default CreateTransaction;
