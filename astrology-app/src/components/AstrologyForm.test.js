import { render, screen, fireEvent } from '@testing-library/react';
import axios from 'axios';
import AstrologyForm from './AstrologyForm';

jest.mock('axios');

test('renders submit button', () => {
  render(<AstrologyForm />);
  expect(screen.getByRole('button', { name: /Πάρε Πρόβλεψη/i })).toBeInTheDocument();
});

test('shows error alert on failed submit', async () => {
  axios.post.mockRejectedValueOnce(new Error('network'));
  render(<AstrologyForm />);
  fireEvent.click(screen.getByRole('button', { name: /Πάρε Πρόβλεψη/i }));
  // Alert should show up eventually
  const alert = await screen.findByRole('alert');
  expect(alert).toBeInTheDocument();
});
