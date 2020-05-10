#pragma once

#include "MsgHandler.h"

#include "context/SelfAuthContext.h"

namespace governikus
{

class MsgHandlerSelfAuth
	: public MsgHandler
{
	private:
		void initSelfAuth();
		void setError(const QLatin1String pError);

	public:
		MsgHandlerSelfAuth();
		explicit MsgHandlerSelfAuth(const QJsonObject& pObj);
		explicit MsgHandlerSelfAuth(const QSharedPointer<SelfAuthContext>& pContext);
};


} // namespace governikus
