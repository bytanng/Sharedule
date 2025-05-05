import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Navbar from "./Navbar";
import Footer from "./Footer";
import { uploadImage } from "../utils/UserRoutes";
import { createItem } from "../utils/ItemRoutes";
import { toast } from "react-hot-toast";

const CreateListing = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    itemName: "",
    itemDescription: "",
    itemPrice: "",
    itemStock: "",
    itemAvailable: true,
    itemImage: "",
  });

  const [errors, setErrors] = useState({});
  const [selectedFile, setSelectedFile] = useState(null);
  const [imagePreviewUrl, setImagePreviewUrl] = useState(null);

  const handleUploadImage = async (event) => {
    const file = event.target.files[0];
    if (!file) {
      return;
    }

    // Show preview immediately
    setSelectedFile(file);
    setImagePreviewUrl(URL.createObjectURL(file));

    try {
      const uploadedImageUrl = await uploadImage(file);
      if (uploadedImageUrl === "Failed to upload image") {
        toast.error("Failed to upload image");
        return;
      }
      setFormData((prev) => ({
        ...prev,
        itemImage: uploadedImageUrl,
      }));
      // Clear any previous image errors
      if (errors.itemImage) {
        setErrors((prev) => ({ ...prev, itemImage: "" }));
      }
    } catch (error) {
      toast.error("Error uploading image");
      console.error("Error uploading image:", error);
    }
  };

  const validateForm = () => {
    const newErrors = {};

    // Validate Item Name (at least 8 chars, must have alphabets)
    if (!formData.itemName.match(/^(?=.*[a-zA-Z])[a-zA-Z0-9\s]{8,}$/)) {
      newErrors.itemName =
        "Item name must be at least 8 characters long and contain alphabets";
    }

    // Validate Description (at least 8 words)
    const wordCount = formData.itemDescription.trim().split(/\s+/).length;
    if (wordCount < 8) {
      newErrors.itemDescription = "Description must be at least 8 words long";
    }

    // Validate Price (0 or above)
    const price = parseFloat(formData.itemPrice);
    if (isNaN(price) || price < 0) {
      newErrors.itemPrice = "Price must be 0 or above";
    }

    // Validate Stock (1 or above)
    const stock = parseInt(formData.itemStock);
    if (isNaN(stock) || stock < 1) {
      newErrors.itemStock = "Stock must be 1 or above";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: type === "checkbox" ? checked : value,
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
      return;
    }

    // Convert price and stock to numbers
    const itemData = {
      ...formData,
      itemPrice: parseFloat(formData.itemPrice),
      itemStock: parseInt(formData.itemStock),
    };

    // Call the API
    const result = await createItem(itemData);

    if (result.success) {
      toast.success("Item created successfully!");
      navigate("/view-listings"); // Redirect to products page
    } else {
      toast.error(result.error);
    }
  };

  return (
    <>
      <Navbar />
      <div className="container my-5">
        <h2 className="text-center mb-4">Create New Listing</h2>
        <div className="row justify-content-center">
          <div className="col-md-8">
            <form onSubmit={handleSubmit}>
              <div className="mb-3">
                <label htmlFor="itemImage" className="form-label">
                  Item Image
                </label>
                {/* Hidden file input */}
                <input
                  type="file"
                  id="fileInput"
                  accept="image/*"
                  style={{ display: "none" }}
                  onChange={handleUploadImage}
                />
                {/* Image preview */}
                {imagePreviewUrl ? (
                  <div className="mb-3">
                    <img
                      src={imagePreviewUrl}
                      alt="Item preview"
                      className="img-thumbnail"
                      style={{
                        maxHeight: "200px",
                        display: "block",
                        marginRight: "auto",
                      }}
                    />
                  </div>
                ) : (
                  <div className="mb-3 text-muted">
                    <div className="border rounded p-3">No image uploaded</div>
                  </div>
                )}
                {/* Upload button */}
                <div className="d-grid gap-2">
                  <label
                    className="btn btn-secondary"
                    htmlFor="fileInput"
                    style={{ cursor: "pointer" }}
                  >
                    {imagePreviewUrl ? "Change Image" : "Upload Image"}
                  </label>
                </div>
                {errors.itemImage && (
                  <div className="invalid-feedback d-block">
                    {errors.itemImage}
                  </div>
                )}
              </div>

              <div className="mb-3">
                <label htmlFor="itemName" className="form-label">
                  Item Name
                </label>
                <input
                  type="text"
                  className={`form-control ${
                    errors.itemName ? "is-invalid" : ""
                  }`}
                  id="itemName"
                  name="itemName"
                  value={formData.itemName}
                  onChange={handleChange}
                  required
                />
                {errors.itemName && (
                  <div className="invalid-feedback">{errors.itemName}</div>
                )}
              </div>

              <div className="mb-3">
                <label htmlFor="itemDescription" className="form-label">
                  Description
                </label>
                <textarea
                  className={`form-control ${
                    errors.itemDescription ? "is-invalid" : ""
                  }`}
                  id="itemDescription"
                  name="itemDescription"
                  value={formData.itemDescription}
                  onChange={handleChange}
                  rows="4"
                  required
                />
                {errors.itemDescription && (
                  <div className="invalid-feedback">
                    {errors.itemDescription}
                  </div>
                )}
              </div>

              <div className="mb-3">
                <label htmlFor="itemPrice" className="form-label">
                  Price
                </label>
                <div className="input-group">
                  <span className="input-group-text">$</span>
                  <input
                    type="number"
                    step="0.01"
                    min="0"
                    className={`form-control ${
                      errors.itemPrice ? "is-invalid" : ""
                    }`}
                    id="itemPrice"
                    name="itemPrice"
                    value={formData.itemPrice}
                    onChange={handleChange}
                    required
                  />
                  {errors.itemPrice && (
                    <div className="invalid-feedback">{errors.itemPrice}</div>
                  )}
                </div>
              </div>

              <div className="mb-3">
                <label htmlFor="itemStock" className="form-label">
                  Stock Quantity
                </label>
                <input
                  type="number"
                  min="1"
                  className={`form-control ${
                    errors.itemStock ? "is-invalid" : ""
                  }`}
                  id="itemStock"
                  name="itemStock"
                  value={formData.itemStock}
                  onChange={handleChange}
                  required
                />
                {errors.itemStock && (
                  <div className="invalid-feedback">{errors.itemStock}</div>
                )}
              </div>

              <div className="mb-3">
                <div className="form-check">
                  <input
                    type="checkbox"
                    className="form-check-input"
                    id="itemAvailable"
                    name="itemAvailable"
                    checked={formData.itemAvailable}
                    onChange={handleChange}
                  />
                  <label className="form-check-label" htmlFor="itemAvailable">
                    Item Available for Sale
                  </label>
                </div>
              </div>

              <div className="text-center">
                <button type="submit" className="btn btn-primary px-5">
                  Create Listing
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

export default CreateListing;
