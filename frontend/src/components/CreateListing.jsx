import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from './Navbar';
import Footer from './Footer';

const CreateListing = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        itemName: '',
        itemDescription: '',
        itemPrice: '',
        itemStock: '',
        itemAvailable: true,
        itemImage: ''
    });

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        // Add your form submission logic here
        console.log('Form submitted:', formData);
        // TODO: Add API call to create listing
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
                                <label htmlFor="itemImage" className="form-label">Image URL</label>
                                <input
                                    type="url"
                                    className="form-control"
                                    id="itemImage"
                                    name="itemImage"
                                    value={formData.itemImage}
                                    onChange={handleChange}
                                    placeholder="https://example.com/image.jpg"
                                />
                                {formData.itemImage && (
                                    <div className="mt-2">
                                        <img
                                            src={formData.itemImage}
                                            alt="Item preview"
                                            className="img-thumbnail"
                                            style={{ maxHeight: '200px' }}
                                        />
                                    </div>
                                )}
                            </div>
                            <div className="mb-3">
                                <label htmlFor="itemName" className="form-label">Item Name</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="itemName"
                                    name="itemName"
                                    value={formData.itemName}
                                    onChange={handleChange}
                                    required
                                />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="itemDescription" className="form-label">Description</label>
                                <textarea
                                    className="form-control"
                                    id="itemDescription"
                                    name="itemDescription"
                                    value={formData.itemDescription}
                                    onChange={handleChange}
                                    rows="4"
                                    required
                                />
                            </div>

                            <div className="mb-3">
                                <label htmlFor="itemPrice" className="form-label">Price</label>
                                <div className="input-group">
                                    <span className="input-group-text">$</span>
                                    <input
                                        type="number"
                                        step="0.01"
                                        min="0"
                                        className="form-control"
                                        id="itemPrice"
                                        name="itemPrice"
                                        value={formData.itemPrice}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="mb-3">
                                <label htmlFor="itemStock" className="form-label">Stock Quantity</label>
                                <input
                                    type="number"
                                    min="0"
                                    className="form-control"
                                    id="itemStock"
                                    name="itemStock"
                                    value={formData.itemStock}
                                    onChange={handleChange}
                                    required
                                />
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