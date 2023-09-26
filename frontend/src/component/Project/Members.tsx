import axios from 'axios';
import React, { useContext, useEffect, useState } from 'react';
import { ProjectContext } from './context/ProjectContext';
import closeImg from '../../img/x.png';
import './css/Members.css';

interface Member {
  cust_nick: string;
  cust_img: string;
}

const Members = ({
  setToggleMembers,
}: {
  setToggleMembers: (toggleMembers: boolean) => void;
}) => {
  const projectId = useContext(ProjectContext);
  const [members, setMembers] = useState<Member[]>();
  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    const url = 'http://172.30.1.20:8099/projectmemberlist';
    const data = { board_id: 51 };
    await axios.post(url, data).then((res) => {
      console.log(res.data);

      setMembers(res.data);
    });
  };
  return (
    <div className="pro-mem-container">
      <div className="pro-mem-header">
        <img
          onClick={() => {
            setToggleMembers(false);
          }}
          src={closeImg}
          alt=""
        ></img>
      </div>
      <div className="pro-mem-main">
        {members &&
          members.map((item) => (
            <div>
              <img
                key={item.cust_nick}
                src={'data:image/;base64,' + item.cust_img}
                alt=""
              ></img>
              <div>{item.cust_nick}</div>
            </div>
          ))}
      </div>
      <div className="pro-mem-footer">asdf</div>
    </div>
  );
};

export default Members;