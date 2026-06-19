import { TestBed } from '@angular/core/testing';
import { expect } from '@jest/globals';
import { SessionInformation } from '../interfaces/sessionInformation.interface';
import { SessionService } from './session.service';

describe('SessionService', () => {
  let service: SessionService;

  const mockUser: SessionInformation = {
    token: 'abc123',
    type: 'Bearer',
    id: 1,
    username: 'test@test.com',
    firstName: 'John',
    lastName: 'Doe',
    admin: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should initialize with isLogged = false', () => {
    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit false from $isLogged() by default', (done) => {
    service.$isLogged().subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });

  it('should log in a user and set sessionInformation', () => {
    service.logIn(mockUser);

    expect(service.isLogged).toBe(true);
    expect(service.sessionInformation).toEqual(mockUser);
  });

  it('should emit true from $isLogged() after logIn', (done) => {
    service.logIn(mockUser);

    service.$isLogged().subscribe(value => {
      expect(value).toBe(true);
      done();
    });
  });

  it('should log out a user and clear sessionInformation', () => {
    service.logIn(mockUser);
    service.logOut();

    expect(service.isLogged).toBe(false);
    expect(service.sessionInformation).toBeUndefined();
  });

  it('should emit false from $isLogged() after logOut', (done) => {
    service.logIn(mockUser);
    service.logOut();

    service.$isLogged().subscribe(value => {
      expect(value).toBe(false);
      done();
    });
  });
});
