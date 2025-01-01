import React, { useState, useEffect } from 'react';
import {
  Container,
  Header,
  StockInfo,
  TabsContainer,
  Tab,
  Table,
  StockButtons,
} from '../styles/StockPageStyle';

const StockPage = () => {
  const [activeTab, setActiveTab] = useState('realtime');
  const [stockData, setStockData] = useState({}); // 모든 종목 데이터
  const [selectedStockId, setSelectedStockId] = useState(null); // 선택된 stockId

  // 종목코드와 이름 매핑
  const stockNameMapping = {
    '005930': '삼성전자',
    '000660': 'SK하이닉스',
    // 필요한 종목코드를 추가
  };

  const handleTabClick = (tab) => {
    setActiveTab(tab);
  };

  const handleStockButtonClick = (stockId) => {
    setSelectedStockId(stockId);
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
      } catch (error) {
        console.error('Redis 초기 데이터 로드 실패:', error);
      }
    };

    fetchInitialData();
  }, []);

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
  }, []);

  // 현재 선택된 stockId 데이터 가져오기
  const currentStockData = selectedStockId
    ? stockData[selectedStockId] || []
    : [];

  return (
    <Container>
      <Header>
        <div className="left">
          <div className="logo">
            {/* <img src={logo} alt="캐치! 주식 로고" /> */}
          </div>
          <div className="nav">
            <div className="button">홈으로</div>
            <div className="button">로그인</div>
          </div>
        </div>
        <div className="search">
          <input type="text" placeholder="삼성" />
        </div>
      </Header>

      <StockButtons>
        {Object.keys(stockNameMapping).map((stockId) => (
          <button
            key={stockId}
            onClick={() => handleStockButtonClick(stockId)}
            className={selectedStockId === stockId ? 'active' : ''}
          >
            {stockNameMapping[stockId]}
          </button>
        ))}
      </StockButtons>

      <StockInfo>
        {currentStockData.length > 0 ? (
          <>
            <div className="stockName">{stockNameMapping[selectedStockId]}</div>
            <div className="current">{currentStockData[0]?.currentPrice}원</div>
            <div className="change-section">
              <div className="label">어제보다</div>
              <div className="change">
                {currentStockData[0]?.fluctuationSign === '2' ? '+' : ''}
                {currentStockData[0]?.fluctuationPrice}원 (
                {currentStockData[0]?.fluctuationRate}%)
              </div>
            </div>
          </>
        ) : (
          // 기본데이터
          <>
            <div className="stockName">기본 데이터</div>
            <div className="current">10000원</div>
            <div className="change-section">
              <div className="label">어제보다</div>
              <div className="change">+500원 (0.5%)</div>
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
        <Table>
          <thead>
            <tr>
              <th>현재가</th>
              <th>전일 대비 부호</th>
              <th>전일 대비</th>
              <th>전일 대비율</th>
              <th>체결 거래량</th>
              <th>체결 시간</th>
            </tr>
          </thead>
          <tbody>
            {currentStockData.map((row, index) => (
              <tr key={index}>
                <td>{row.currentPrice}</td>
                <td>{row.fluctuationSign}</td>
                <td>{row.fluctuationPrice}</td>
                <td>{row.fluctuationRate}</td>
                <td>{row.transactionVolume}</td>
                <td>{row.tradingTime}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
};

export default StockPage;
