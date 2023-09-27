import React, { useEffect, useState } from 'react';
import '../css/Main.css';
import Header from './Header';
import Banner from './Banner';
import Contents from './Contents';
import CategoryBox from './CategoryBox';
import axios from 'axios';
import TopPosts from './TopPosts';
import Footer from './Footer';
import Cookies from 'js-cookie';

type MainProps = {};

const Main: React.FC<MainProps> = () => {
  const [currentPage, setCurrentPage] = useState<number>(1);
  const [categoryData, setCategoryData] = useState<any[]>([]);
  const [selectedLanguage, setSelectedLanguage] = useState<string | null>(null);
  const [selectedPosition, setSelectedPosition] = useState<string | null>(null);
  const [isBookmarked, setIsBookmarked] = useState<boolean>(false);
  const [isApplied, setIsApplied] = useState<boolean>(false);
  const [isRefreshing, setIsRefreshing] = useState<boolean>(false);
  const [isMyPosts, setIsMyPosts] = useState<boolean>(false);
  const maxEndpoint = 99;
  const pageSize = 0;
  const initialLoad = useState<boolean>(false)[0];

  // 북마크 데이터를 저장할 상태 추가
  const [bookmarkData, setBookmarkData] = useState<any[]>([]);
  const [Data1, setData1] = useState<any[]>([]);
  const [Data2, setData2] = useState<any[]>([]);

  const handlePageChange = (page: number): void => {
    setCurrentPage(page);
  };

  const handleExpandPageClick = async (): Promise<void> => {
    if (currentPage < maxEndpoint && !isRefreshing && !isApplied && !isBookmarked && !isMyPosts) {
      const nextPage = currentPage + 6;
      setIsRefreshing(true);

      const requestData = {
        skill_name: selectedLanguage,
        board_position: selectedPosition,
        endpoint: nextPage
      };

      try {
        requestData.endpoint *= pageSize;
        const response = await axios.post('http://localhost:8099/select', requestData);
        const fetchedData = response.data.map((item: any) => {
          return {
            id: item.board_id,
            name: item.board_title,
            title: item.board_title,
            content: item.board_content,
            board_deadline: item.board_deadline,
            board_dt: item.board_dt,
            board_members: item.board_members,
            board_openlink: item.board_openlink,
            board_period: item.board_period,
            board_position: item.board_position,
            board_views: item.board_views,
            cust_id: item.cust_id,
            pro_img: item.pro_img,
            pro_link: item.pro_link,
            pro_title: item.pro_title,
            cust_nick: item.cust_nick
          };
        });

        if (fetchedData.length === 0) {
          console.warn("No data received.");
        } else {
          setCategoryData((prevData) => [...prevData, ...fetchedData]);
          setCurrentPage(nextPage);
        }
      } catch (error) {
        console.error("Error fetching data:", error);

        setSelectedLanguage(null);
        setSelectedPosition(null);
        setCurrentPage(1);

        const requestData = {
          skill_name: null,
          board_position: null,
          endpoint: 1
        };
        fetchData(requestData);
      } finally {
        setIsRefreshing(false);
      }
    }
  };

  useEffect(() => {
    const initialRequestData = {
      skill_name: selectedLanguage,
      board_position: selectedPosition,
      endpoint: currentPage
    };

    fetchData(initialRequestData);
    initialLoad && handleExpandPageClick();
  }, [selectedLanguage, selectedPosition, currentPage, initialLoad, isBookmarked, isApplied, isMyPosts]);

  const updateCategoryData = (selectedCategory: string | null) => {
    setSelectedLanguage(selectedCategory);
    setSelectedPosition(selectedCategory);
    setCurrentPage(1);
    setCategoryData([]);
  };

  const handleLoginButtonClick = (): void => {
    // 로그인 버튼 클릭 핸들러
  };

  const fetchDataAndUpdateState = async (url: string, stateUpdater: (data: any) => void) => {
    try {
      const custId = Cookies.get('CUST_ID');
      const response = await axios.get(url + `?cust_id=${custId}`);
      if (response.status === 200) {
        const fetchedData = response.data.map((item: any) => {
          return {
            id: item.board_id,
            name: item.board_title,
            title: item.board_title,
            content: item.board_content,
            board_deadline: item.board_deadline,
            board_dt: item.board_dt,
            board_members: item.board_members,
            board_openlink: item.board_openlink,
            board_period: item.board_period,
            board_position: item.board_position,
            board_views: item.board_views,
            cust_id: item.cust_id,
            pro_img: item.pro_img,
            pro_link: item.pro_link,
            pro_title: item.pro_title,
            cust_nick: item.cust_nick
          };
        });

        if (fetchedData.length === 0) {
          console.warn("No data received.");
        } else {
          stateUpdater(fetchedData);
        }
      } else {
        console.error("Data retrieval failed.");
      }
      console.log(response);
    } catch (error) {
      console.error("Data retrieval error:", error);
    }
  };

  const fetchData = async (requestData: any) => {
    try {
      requestData.endpoint *= pageSize;
      const response = await axios.post('http://localhost:8099/select', requestData);
      const fetchedData = response.data.map((item: any) => {
        return {
          id: item.board_id,
          name: item.board_title,
          title: item.board_title,
          content: item.board_content,
          board_deadline: item.board_deadline,
          board_dt: item.board_dt,
          board_members: item.board_members,
          board_openlink: item.board_openlink,
          board_period: item.board_period,
          board_position: item.board_position,
          board_views: item.board_views,
          cust_id: item.cust_id,
          pro_img: item.pro_img,
          pro_link: item.pro_link,
          pro_title: item.pro_title,
          cust_nick: item.cust_nick,
          skill_names: item.skill_names
        };
      });
      console.log(response);

      if (fetchedData.length === 0) {
        console.warn("No data received.");
      } else {
        setCategoryData(fetchedData);
      }
    } catch (error) {
      console.error("Error fetching data:", error);

      setSelectedLanguage(null);
      setSelectedPosition(null);
      setCurrentPage(1);

      const requestData = {
        skill_name: null,
        board_position: null,
        endpoint: 1
      };
      fetchData(requestData);
    }
  };

  const handleMoreButtonClick = (): void => {
    handleExpandPageClick();
  };

  const handleBookmarkToggle = async (): Promise<void> => {
    setIsBookmarked(!isBookmarked);
    setIsApplied(false);   // 다른 토글 상태 초기화
    setIsMyPosts(false);   // 다른 토글 상태 초기화

    if (isBookmarked) {
      setBookmarkData([]);
    } else {
      await fetchDataAndUpdateState('http://localhost:8099/bookmark', setBookmarkData);
    }
  };

  const handleAppliedToggle = async (): Promise<void> => {
    setIsApplied(!isApplied);
    setIsBookmarked(false); // 다른 토글 상태 초기화
    setIsMyPosts(false);   // 다른 토글 상태 초기화

    if (isApplied) {
      setData1([]);
    } else {
      await fetchDataAndUpdateState('http://localhost:8099/apply', setCategoryData);
    }
  };

  const onMyPostsToggle = async (): Promise<void> => {
    setIsMyPosts(!isMyPosts);
    setIsApplied(false);   // 다른 토글 상태 초기화
    setIsBookmarked(false); // 다른 토글 상태 초기화

    if (isMyPosts) {
      setData2([]);
    } else {
      await fetchDataAndUpdateState('http://localhost:8099/writelist', setCategoryData);
    }
  };

  return (
    <div>
      <Header onLoginButtonClick={handleLoginButtonClick} />
      <Banner />
      <TopPosts />
      <CategoryBox
        onUpdateData={updateCategoryData}
        setSelectedLanguage={setSelectedLanguage}
        setSelectedPosition={setSelectedPosition}
        isBookmarked={isBookmarked}
        isApplied={isApplied}
        isMyPosts={isMyPosts}
        onBookmarkToggle={handleBookmarkToggle}
        onAppliedToggle={handleAppliedToggle}
        onMyPostsClick={onMyPostsToggle}
      />
      <div className="contents-container">
        <Contents categoryData={
          isMyPosts ? Data2 : (isBookmarked ? bookmarkData : (isApplied ? Data1 : categoryData))
        } />
      </div>
      <div className={`more-button-container ${isMyPosts || isBookmarked || isApplied ? 'hidden' : ''}`}>
        <button
          onClick={handleMoreButtonClick}
          disabled={currentPage === maxEndpoint}
          className="more-button"
        >
          더 보기
        </button>
      </div>
      <Footer />
    </div>
  );
};
export default Main;
