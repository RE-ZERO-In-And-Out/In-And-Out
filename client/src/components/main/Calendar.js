import { Link } from "react-router-dom";

export default function Calendar() {
  return (
    <>
      <div>Calendar</div>
      <Link to="/setting">ProfileChange</Link>
      <br />
      <Link to="/main/inout">Inout</Link>
      <br />
      <Link to="/main/report">Report</Link>
    </>
  );
}
