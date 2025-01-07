import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { StockInfo, TabsContainer, Tab } from '../styles/StockPageStyle';
import '../styles/MainVars.css';
import '../styles/MainStyle.css';

const StockPage = () => {
  const { stockName } = useParams(); // URL에서 stockName 가져오기
  const [searchTerm, setSearchTerm] = useState('');
  const [activeTab, setActiveTab] = useState('realtime');
  const [stockData, setStockData] = useState({}); // 모든 종목 데이터
  const [selectedStock, setSelectedStock] = useState(null); // 선택된 종목 데이터
  const navigate = useNavigate();

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handleSearch = () => {
    if (searchTerm.trim()) {
      navigate(`/search?query=${searchTerm}`);
    }
  };
  // Redis에서 초기 데이터 로드
  useEffect(() => {
    const fetchInitialData = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/redis-data');
        const data = await response.json();
        const parsedData = {};

        // 데이터를 stockId를 키로 하는 형태로 변환
        for (const [key, value] of Object.entries(data)) {
          parsedData[key.replace('stock:', '')] = value.map(JSON.parse); // Redis key에서 "stock:" 제거
        }

        setStockData(parsedData);
        // 선택된 stockName과 일치하는 데이터를 설정
        const selected = Object.values(parsedData).find(
          (stock) => stock[0]?.name === stockName
        );
        if (selected) {
          setSelectedStock(selected[0]); // 첫 번째 데이터를 기본으로 설정
        }
      } catch (error) {
        console.error('Redis 초기 데이터 로드 실패:', error);
      }
    };

    fetchInitialData();
  }, [stockName]);

  // WebSocket 연결
  useEffect(() => {
    const socket = new WebSocket('ws://localhost:8080/ws/stock');

    socket.onmessage = (event) => {
      const data = JSON.parse(event.data);

      setStockData((prevData) => {
        const updatedStockData = {
          ...prevData,
          [data.stockId]: [data, ...(prevData[data.stockId] || []).slice(0, 4)], // 최신 5개 데이터 유지
        };

        // WebSocket으로 받은 데이터가 현재 선택된 stockName과 일치하면 업데이트
        if (data.name === stockName) {
          setSelectedStock(data);
        }

        return updatedStockData;
      });
    };

    socket.onopen = () => {
      console.log('WebSocket 연결 성공');
    };

    socket.onerror = (error) => {
      console.error('WebSocket 에러:', error);
    };

    socket.onclose = () => {
      console.log('WebSocket 연결 종료');
    };

    return () => {
      socket.close();
    };
  }, [stockName]);

  return (
    <div className="_0-1-home">
      <div className="frame-45">
        {/* Header */}
        <div className="gnb">
          <div className="frame-11">
            <div className="frame-26">
              <img className="image-6" src="/image-60.png" alt="Logo" />
              <div className="frame-10">
                <div className="frame-9">
                  <div
                    className="gnb-button"
                    onClick={() => navigate('/')}
                    style={{ cursor: 'pointer' }}
                  >
                    홈으로
                  </div>
                  <div
                    className="gnb-button"
                    onClick={() => navigate('/login')}
                    style={{ cursor: 'pointer' }}
                  >
                    로그인
                  </div>
                </div>
              </div>
            </div>
            <div className="search-bar">
              <div className="frame-1">
                <img
                  className="search-01"
                  src="/search-010.svg"
                  alt="Search Icon"
                  style={{ cursor: 'pointer' }}
                  onClick={handleSearch}
                />
                <input
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  placeholder="검색하실 종목 이름을 입력해 주세요."
                  className="div2"
                />
              </div>
            </div>
          </div>
        </div>

        {/* Stock Info */}

        <StockInfo>
          {selectedStock ? (
            <>
              <div className="stockName">{selectedStock.name}</div>
              <div className="current">{selectedStock.price}원</div>
              <div className="change-section">
                <div className="label">어제보다</div>
                <div className="change">
                  {selectedStock.change} ({selectedStock.points})
                </div>
              </div>
            </>
          ) : (
            <>
              <div className="stockName">종목이름</div>
              <div className="current">9999원</div>
              <div className="change-section">
                <div className="label">어제보다</div>
                <div className="change">+100</div>
              </div>
            </>
          )}
        </StockInfo>

        <TabsContainer>
          <Tab
            active={activeTab === 'realtime'}
            onClick={() => handleTabClick('realtime')}
          >
            실시간 체결정보
          </Tab>
          <Tab
            active={activeTab === 'daily'}
            onClick={() => handleTabClick('daily')}
          >
            일별 시세조회
          </Tab>
        </TabsContainer>

        {activeTab === 'realtime' && (
          <div className="main-content">
            <div className="stock-ranking">
              <table className="stock-table">
                <thead>
                  <tr>
                    <th>종목</th>
                    <th>거래량</th>
                    <th>주가</th>
                    <th>등락</th>
                    <th>체결 시간</th>
                  </tr>
                </thead>
                <tbody>
                  {selectedStock &&
                    stockData[selectedStock.stockId]?.map((data, index) => (
                      <tr key={index}>
                        <td>{data.name}</td>
                        <td>{data.volume}</td>
                        <td>{data.price}</td>
                        <td
                          style={{
                            color: data.change.includes('-')
                              ? '#2175F2'
                              : '#FF4726',
                          }}
                        >
                          {data.change} <span>{data.points}</span>
                        </td>
                        <td>{data.tradingTime}</td>
                      </tr>
                    ))}
                </tbody>
              </table>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default StockPage;
