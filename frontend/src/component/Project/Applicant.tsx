import React from 'react';
import { Member } from './Settings';
import axios from 'axios';
import profileImg from '../../img/profilePicture.png';
import { addNotification } from './functions/firebaseCRUD';
import Cookies from 'js-cookie';

//프로젝트 응모 거절로 응모테이블에서 삭제하기 (board_id, cust_id)
//  @PostMapping("applydecline")
//  public int applyDecline(@RequestBody JSONObject obj) {
//     int cnt = service.applyDecline((Integer)obj.get("board_id"), (String)obj.get("cust_id"));
//     return cnt;
//  }

const Applicant = ({
  projectId,
  index,
  applicants,
  setApplicants,
}: {
  projectId: string;
  index: number;
  applicants: Member[];
  setApplicants: (applicants: Member[]) => void;
}) => {
  const accept = async () => {
    const url = `${process.env.REACT_APP_URL_8099}/projectaccept`;
    const data = {
      board_id: parseInt(projectId),
      cust_id: applicants[index].cust_id,
    };
    await axios.post(url, data).then(() => {
      const nowDate = new Date();
      const notiData = {
        sender: Cookies.get('CUST_ID')!,
        receiver: applicants[index].cust_id,
        board_id: parseInt(projectId),
        contents: `님 께서 프로젝트 팀원으로 수락하였습니다.`,
        date: nowDate.toLocaleString(),
        checked: false,
      };
      addNotification(
        `notification/${applicants[index].cust_id.split('.')[0]}`,
        notiData
      );
      setApplicants(
        [...applicants].filter(
          (item) => item.cust_id !== applicants[index].cust_id
        )
      );
    });
  };

  const decline = async () => {
    const url = `${process.env.REACT_APP_URL_8099}/applydecline`;
    const data = {
      board_id: parseInt(projectId),
      cust_id: applicants[index].cust_id,
    };
    await axios.post(url, data).then(() => {
      setApplicants(
        [...applicants].filter(
          (item) => item.cust_id !== applicants[index].cust_id
        )
      );
    });
  };

  return (
    <div className="applicant-div">
      <div>
        <img
          className="setting-profileImg"
          src={
            applicants[index].cust_img
              ? 'data:image/;base64,' + applicants[index].cust_img
              : profileImg
          }
          alt=""
        ></img>
      </div>
      <div className="setting-usernick">{applicants[index].cust_nick}</div>
      <div onClick={accept}>
        <img
          className="acceptdelcine"
          src={process.env.PUBLIC_URL + '/projectImg/accept.png'}
          alt=""
        ></img>
      </div>
      <div onClick={decline}>
        <img
          className="acceptdelcine"
          src={process.env.PUBLIC_URL + '/projectImg/decline.png'}
          alt=""
        ></img>
      </div>
    </div>
  );
};

export default Applicant;
