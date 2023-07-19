import { getVerificationCode } from 'src/smsUserConsentModule';

it.todo('write a test');

describe('getVerificationCode', () => {
  it('should give the sms code correctly', () => {
    const msg = '<#> 5436 ccvasdwSk';
    expect(getVerificationCode(msg, 4)).toStrictEqual('5436');
  });
});
