import React, { Dispatch, SetStateAction, useState } from 'react';

type prop = {
    name: string,
    partyCode: string,
    setName: Dispatch<SetStateAction<string>>,
    setPartyCode: Dispatch<SetStateAction<string>>,
    joinParty: () => void,
    createParty: () => void,
}

const PartyForm: React.FC<prop> = ({name, partyCode, setName, setPartyCode, joinParty, createParty}) => {
  // State to hold the name and party code inputs

  // Handle input changes
  const handleNameChange = (event: any) => {
    setName(event.target.value);
  };

  const handlePartyCodeChange = (event: any) => {
    setPartyCode(event.target.value);
  };

  return (
    <div className="container" style={{ padding: '20px', maxWidth: '400px', margin: 'auto' }}>
      
      <div style={{ marginBottom: '10px' }}>
        <label htmlFor="name" style={{ display: 'block', fontWeight: 'bold' }}></label>
        <input
          type="text"
          id="name"
          value={name}
          onChange={handleNameChange}
          placeholder="Enter your name"
          style={{ backgroundColor: '#3E3E3E', color: '#FFFFFF', width: '100%', padding: '8px', fontSize: '14px', marginBottom: '10px' }}
        />
      </div>

      <div style={{ marginBottom: '10px' }}>
        <label htmlFor="partyCode" style={{ display: 'block', fontWeight: 'bold' }}></label>
        <input
          type="text"
          id="partyCode"
          value={partyCode}
          onChange={handlePartyCodeChange}
          placeholder="Enter party code"
          style={{ backgroundColor: '#3E3E3E', color: '#FFFFFF', width: '100%', padding: '8px', fontSize: '14px', marginBottom: '10px' }}
        />
      </div>

      <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', justifyContent: 'center'}}>
        <button
          onClick={joinParty}
          style={{
            backgroundColor: '#3e3e3e',
            color: 'white',
            padding: '10px 15px',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            marginRight: '10px'
          }}
        >
          Join
        </button>
        <button
          onClick={createParty}
          style={{
            backgroundColor: '#3e3e3e',
            color: 'white',
            padding: '10px 15px',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          Create
        </button>
      </div>
    </div>
  );
};

export default PartyForm;

