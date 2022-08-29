// import { useState } from 'react';

const ItemRow = ({ item, updateOrderAmount }) => {
  const { sku, name, stock, capacity } = item;

  return (
    <>
      <tr>
        <td>{sku}</td>
        <td>{name}</td>
        <td>{stock}</td>
        <td>{capacity}</td>
        <td>
          <input type='number' onChange={(e) => updateOrderAmount(sku, e.target.value)}></input>
        </td>
      </tr>
    </>
  );
};

export default ItemRow;
