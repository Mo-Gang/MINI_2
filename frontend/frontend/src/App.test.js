import { render, screen } from '@testing-library/react';
import App from './App';

test('renders learn react link', () => {
  render(<App />);
  const linkElement = screen.getByText(/실시간 대응형 공장 안전 모니터링 시스템/i);
  expect(linkElement).toBeInTheDocument();
});
