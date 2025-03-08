import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from 'react-router-dom';
import { Footer, Navbar } from "../components";
import { getItem, editItem } from "../utils/ItemRoutes";
import { uploadImage } from "../utils/UserRoutes";
import Skeleton from "react-loading-skeleton";
import { toast } from 'react-hot-toast';

const EditItem = () => {

    const navigate = useNavigate();
    const { id } = useParams();
    const [errors, setErrors] = useState({});
    const [formData, setFormData] = useState({
            itemName: null,
            itemDescription: null,
            itemPrice: null,
            itemStock: null,
            itemAvailable: null,
            itemImage: null
        });
    const [imagePreviewUrl, setImagePreviewUrl] = useState(null);

    useEffect(() => {
        const showItem = async () => {
            try {
                const data = await getItem(id);
                setFormData({
                    itemName: data.itemName,
                    itemDescription: data.itemDescription,
                    itemPrice: data.itemPrice,
                    itemStock: data.itemStock,
                    itemAvailable: data.itemAvailable,
                    itemImage: data.itemImage == '' ? null : data.itemImage
                })
            } catch (error) {
                setFormData(null);
            }
        };

        showItem();
    }, [id]);

    const validateForm = () => {
        const newErrors = {};

        if (!formData.itemName.match(/^(?=.*[a-zA-Z])[a-zA-Z0-9\s]{8,}$/)) {
            newErrors.itemName = "Item name must be at least 8 characters long and contain alphabets";
        }

        const wordCount = formData.itemDescription.trim().split(/\s+/).length;
        if (wordCount < 8) {
            newErrors.itemDescription = "Description must be at least 8 words long";
        }

        const price = parseFloat(formData.itemPrice);
        if (isNaN(price) || price < 0) {
            newErrors.itemPrice = "Price must be 0 or above";
        }

        const stock = parseInt(formData.itemStock);
        if (isNaN(stock) || stock < 1) {
            newErrors.itemStock = "Stock must be 1 or above";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            toast.error('Please fix the errors in the form');
            return;
        }

        const itemData = {
            ...formData,
            itemPrice: parseFloat(formData.itemPrice),
            itemStock: parseInt(formData.itemStock)
        };

        const result = await editItem(id, itemData);
        
        if (result.success) {
            toast.success('Item updated successfully!');
            navigate(`/item/${id}`);
        } else {
            toast.error(result.error);
        }
    }

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: type === 'checkbox' ? checked : value
        }));
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    const cancel = () => {
        navigate(`/item/${id}`);
    }

    const handleUploadImage = async (event) => {
        const file = event.target.files[0];
        if (!file) {
            return;
        }

        setImagePreviewUrl(URL.createObjectURL(file));

        try {
            const uploadedImageUrl = await uploadImage(file);
            if (uploadedImageUrl === "Failed to upload image") {
                toast.error("Failed to upload image");
                return;
            }
            setFormData(prev => ({
                ...prev,
                itemImage: uploadedImageUrl
            }));
            if (errors.itemImage) {
                setErrors(prev => ({ ...prev, itemImage: '' }));
            }
        } catch (error) {
            toast.error("Error uploading image");
            console.error("Error uploading image:", error);
        }
    };

    return (
        <>
        <Navbar />
        <div className="container my-5">
            <h2 className="text-center mb-4">Edit Item Listing</h2>
            <div className="row justify-content-center">
                <div className="col-md-8">
                    <form onSubmit={handleSubmit}>
                        <div className="mb-3">
                            <label htmlFor="itemImage" className="form-label">Item Image</label>
                            <input
                                type="file"
                                id="fileInput"
                                accept="image/*"
                                style={{ display: 'none' }}
                                onChange={handleUploadImage}
                            />
                            {imagePreviewUrl ? (
                                <div className="mb-3">
                                    <img
                                        src={imagePreviewUrl}
                                        alt="Item preview"
                                        className="img-thumbnail"
                                        style={{ maxHeight: '200px', display: 'block', marginRight: 'auto' }}
                                    />
                                </div>
                            ) : (
                                <div className="mb-3 text-muted">
                                    <div className="border rounded p-3">
                                        No image uploaded
                                    </div>
                                </div>
                            )}
                            <div className="d-grid gap-2">
                                <label 
                                    className="btn btn-secondary"
                                    htmlFor="fileInput"
                                    style={{ cursor: 'pointer' }}
                                >
                                    {imagePreviewUrl ? 'Change Image' : 'Upload Image'}
                                </label>
                            </div>
                            {errors.itemImage && <div className="invalid-feedback d-block">{errors.itemImage}</div>}
                        </div>

                        <div className="mb-3">
                            <label htmlFor="itemName" className="form-label">Item Name</label>
                            <input
                                type="text"
                                className={`form-control ${errors.itemName ? 'is-invalid' : ''}`}
                                id="itemName"
                                name="itemName"
                                value={formData?.itemName}
                                onChange={handleChange}
                                required
                            />
                            {errors.itemName && <div className="invalid-feedback">{errors.itemName}</div>}
                        </div>

                        <div className="mb-3">
                            <label htmlFor="itemDescription" className="form-label">Description</label>
                            <textarea
                                className={`form-control ${errors.itemDescription ? 'is-invalid' : ''}`}
                                id="itemDescription"
                                name="itemDescription"
                                value={formData?.itemDescription}
                                onChange={handleChange}
                                rows="4"
                                required
                            />
                            {errors.itemDescription && <div className="invalid-feedback">{errors.itemDescription}</div>}
                        </div>

                        <div className="mb-3">
                            <label htmlFor="itemPrice" className="form-label">Price</label>
                            <div className="input-group">
                                <span className="input-group-text">$</span>
                                <input
                                    type="number"
                                    step="0.01"
                                    min="0"
                                    className={`form-control ${errors.itemPrice ? 'is-invalid' : ''}`}
                                    id="itemPrice"
                                    name="itemPrice"
                                    value={formData?.itemPrice}
                                    onChange={handleChange}
                                    required
                                />
                                {errors.itemPrice && <div className="invalid-feedback">{errors.itemPrice}</div>}
                            </div>
                        </div>

                        <div className="mb-3">
                            <label htmlFor="itemStock" className="form-label">Stock Quantity</label>
                            <input
                                type="number"
                                min="1"
                                className={`form-control ${errors.itemStock ? 'is-invalid' : ''}`}
                                id="itemStock"
                                name="itemStock"
                                value={formData?.itemStock}
                                onChange={handleChange}
                                required
                            />
                            {errors.itemStock && <div className="invalid-feedback">{errors.itemStock}</div>}
                        </div>

                        <div className="mb-3">
                            <div className="form-check">
                                <input
                                    type="checkbox"
                                    className="form-check-input"
                                    id="itemAvailable"
                                    name="itemAvailable"
                                    checked={formData?.itemAvailable}
                                    onChange={handleChange}
                                />
                                <label className="form-check-label" htmlFor="itemAvailable">
                                    Item Available for Sale
                                </label>
                            </div>
                        </div>
                        <div className="container col-md-6 col-md-6 py-5">
                            <div className="row">
                                <div className="d-flex justify-content-center">
                                    <button type="submit" className="btn btn-primary m-1">
                                        Edit Listing
                                    </button>
                                    <button type="button" onClick={() => cancel()} className="btn btn-danger m-1">
                                        Cancel
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <Footer />
        </> 
  )
}

export default EditItem
