import React from 'react';

function ListofPlayer(props) {
    const { player } = props;
    return (
        <tr>
            <td>{player.id}</td>
            <td>{player.name}</td>
            <td>{player.country}</td>
            <td>{player.role}</td>
            <td>{player.matches}</td>
            <td>{player.runs}</td>
            <td>{player.wickets}</td>
        </tr>
    );
}

export default ListofPlayer;
