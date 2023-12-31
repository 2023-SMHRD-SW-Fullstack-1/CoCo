import React, { useEffect, useState } from 'react';
import '../../css/Board.css';
import linkIcon from '../../img/linkIcon.png';

const Table = ({ boardData }) => {
  const [member, setMember] = useState();
  const [deadLine, setDeadLine] = useState();
  const [position, setPosition] = useState();
  const [period, setPeriod] = useState('');
  const [skillName, setSkillName] = useState([]);

  const fetchData = async () => {
    console.log('Table', boardData);
    setMember(boardData.TB_BOARD.board_members);
    setPeriod(boardData.TB_BOARD.board_period);
    setDeadLine(boardData.TB_BOARD.board_deadline);
    setPosition(boardData.TB_BOARD.board_position);
    setSkillName(boardData.TB_BOARD_SKILL);
  };

  useEffect(() => {
    fetchData();
  }, []);

  const linkStyle = {
    color: 'gray',
    // textDecoration: 'none',
  };

  return (
    <table className="table">
      <tbody>
        <tr>
          <th className="widthSize">모집인원</th>
          <td>{member}명</td>
          <th className="widthSize">모집마감</th>
          <td>{deadLine}</td>
        </tr>
        <tr>
          <th>모집분야</th>
          <td>{position}</td>
          <th>진행기간</th>
          <td>{period}개월</td>
        </tr>
        <tr>
          <th>필요스킬</th>
          <td className="skillImg">
            {skillName.map((skill) => (
              // console.log(process.env.PUBLIC_URL +`/src/img/skillImg/${skill}.svg`)
              <img src={process.env.PUBLIC_URL + `/skillImg/${skill}.svg`} />
            ))}
          </td>
          <th>연락방법</th>
          <td>
            <a href="https://open.kakao.com/o/sb93t0Gf" style={linkStyle}>
              오픈톡
            </a>
            <img src={linkIcon} alt="linkIcon" className="linkIcon" />
          </td>
        </tr>
      </tbody>
    </table>
  );
};

export default Table;
