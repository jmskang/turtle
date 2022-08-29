import { useState } from 'react';
import ItemRow from './ItemRow';
import axios from 'axios';

export default function Challenge() {
  // const [candies, setCandies] = useState([]);
  const [candies, setCandies] = useState([]);

  const [restockCost, setRestockCost] = useState(0);
  const [orderDetails, setOrderDetails] = useState({});

  const fetchLowStockCandies = async () => {
    const response = await axios.get('http://localhost:4567/low-stock');
    const candy = response.data;
    setCandies(candy);
  };

  const fetchRestockCost = async () => {
    const response = await axios.post('http://localhost:4567/restock-cost', orderDetails);
    const newRestockCost = response.data;
    setRestockCost(newRestockCost);
  };

  const updateOrderAmount = (sku, amount) => {
    const newDetails = { ...orderDetails };
    newDetails[sku] = amount;
    setOrderDetails(newDetails);
  };

  return (
    <>
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount</td>
          </tr>
        </thead>
        <tbody>
          {
            /*
          TODO: Create an <ItemRow /> component that's rendered for every inventory item. The component
          will need an input element in the Order Amount column that will take in the order amount and
          update the application state appropriately.
          */
            candies.map((item) => {
              return <ItemRow key={item.sku} item={item} updateOrderAmount={updateOrderAmount} />;
            })
          }
        </tbody>
      </table>
      {/* TODO: Display total cost returned from the server */}
      <div>Total Cost: ${parseFloat(restockCost).toFixed(2)}</div>
      {/*
      TODO: Add event handlers to these buttons that use the Java API to perform their relative actions.
      */}
      <button onClick={fetchLowStockCandies}>Get Low-Stock Items</button>
      <button onClick={fetchRestockCost}>Determine Re-Order Cost</button>
    </>
  );
}
