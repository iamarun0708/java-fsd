import { useParams } from 'react-router-dom';
import trainersMock from './TrainersMock';

function TrainerDetails() {
    const { id } = useParams();
    const trainer = trainersMock.find(t => t.Trainerid === id);

    if (!trainer) {
        return <div><h2>Trainer not found</h2></div>;
    }

    return (
        <div>
            <h2>Trainer Details</h2>
            <p><b>Trainer ID:</b> {trainer.Trainerid}</p>
            <p><b>Name:</b> {trainer.Name}</p>
            <p><b>Email:</b> {trainer.Email}</p>
            <p><b>Phone:</b> {trainer.Phone}</p>
            <p><b>Technology:</b> {trainer.Technology}</p>
            <p><b>Skills:</b> {trainer.Skill.join(', ')}</p>
        </div>
    );
}

export default TrainerDetails;
